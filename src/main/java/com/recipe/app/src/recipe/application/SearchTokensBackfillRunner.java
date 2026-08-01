package com.recipe.app.src.recipe.application;

import com.recipe.app.src.common.utils.KoreanTokenizer;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.List;
import java.util.function.Function;

@Slf4j
@Component
@ConditionalOnProperty(value = "recipe.migration.search-tokens.enabled", havingValue = "true")
public class SearchTokensBackfillRunner implements ApplicationRunner {

    private static final int BATCH_SIZE = 500;
    private static final Function<String, String> NORI_TOKENIZE = KoreanTokenizer::tokenize;
    private static final Function<String, String> SIMPLE_NORMALIZE = text -> text == null ? "" : text.toLowerCase().trim();

    @PersistenceContext
    private EntityManager em;

    private final TransactionTemplate transactionTemplate;

    public SearchTokensBackfillRunner(PlatformTransactionManager transactionManager) {
        this.transactionTemplate = new TransactionTemplate(transactionManager);
    }

    @Override
    public void run(ApplicationArguments args) {

        log.info("SearchTokens backfill started");
        backfill("Recipe", "recipeId", "searchTokens", new String[]{"recipeNm", "introduction"}, NORI_TOKENIZE);
        // RecipeIngredient 는 단일 명사 위주라 nori stopword 정책이 도메인 단어를 제거해버림 (예: "갓","다시다").
        // 단순 정규화로 처리.
        backfill("RecipeIngredient", "recipeIngredientId", "searchTokens", new String[]{"ingredientName"}, SIMPLE_NORMALIZE);
        backfill("BlogRecipe", "blogRecipeId", "searchTokens", new String[]{"title", "description"}, NORI_TOKENIZE);
        backfill("YoutubeRecipe", "youtubeRecipeId", "searchTokens", new String[]{"title", "description"}, NORI_TOKENIZE);

        // 제목 우선 정렬용 titleSearchTokens (제목만 토큰화). 재료(RecipeIngredient)는 제목 개념이 없어 제외.
        backfill("Recipe", "recipeId", "titleSearchTokens", new String[]{"recipeNm"}, NORI_TOKENIZE);
        backfill("BlogRecipe", "blogRecipeId", "titleSearchTokens", new String[]{"title"}, NORI_TOKENIZE);
        backfill("YoutubeRecipe", "youtubeRecipeId", "titleSearchTokens", new String[]{"title"}, NORI_TOKENIZE);
        log.info("SearchTokens backfill done");
    }

    private void backfill(String table, String pk, String targetColumn, String[] sourceColumns, Function<String, String> tokenizer) {

        log.info("[{}.{}] backfill started", table, targetColumn);
        long lastId = 0L;
        long total = 0L;
        while (true) {
            long startId = lastId;
            BatchResult r = transactionTemplate.execute(status -> processBatch(table, pk, targetColumn, sourceColumns, startId, tokenizer));
            if (r == null || r.processed == 0) break;
            lastId = r.lastId;
            total += r.updated;
            log.info("[{}.{}] progress lastId={} updatedSoFar={}", table, targetColumn, lastId, total);
        }
        log.info("[{}.{}] backfill done. updated={}", table, targetColumn, total);
    }

    private BatchResult processBatch(String table, String pk, String targetColumn, String[] sourceColumns, long startId, Function<String, String> tokenizer) {

        String columnList = String.join(", ", sourceColumns);
        String selectSql = "SELECT " + pk + ", " + columnList +
                " FROM " + table +
                " WHERE " + pk + " > :startId" +
                " ORDER BY " + pk + " ASC" +
                " LIMIT :batchSize";

        @SuppressWarnings("unchecked")
        List<Object[]> rows = em.createNativeQuery(selectSql)
                .setParameter("startId", startId)
                .setParameter("batchSize", BATCH_SIZE)
                .getResultList();

        if (rows.isEmpty()) return new BatchResult(0, 0, startId);

        String updateSql = "UPDATE " + table + " SET " + targetColumn + " = :tokens WHERE " + pk + " = :id";

        int updated = 0;
        long lastId = startId;
        for (Object[] row : rows) {
            Long id = ((Number) row[0]).longValue();
            StringBuilder text = new StringBuilder();
            for (int i = 1; i < row.length; i++) {
                String s = row[i] == null ? "" : row[i].toString();
                if (text.length() > 0) text.append(' ');
                text.append(s);
            }
            String tokens = tokenizer.apply(text.toString());
            em.createNativeQuery(updateSql)
                    .setParameter("tokens", tokens)
                    .setParameter("id", id)
                    .executeUpdate();
            lastId = id;
            updated++;
        }
        return new BatchResult(rows.size(), updated, lastId);
    }

    private static class BatchResult {
        final int processed;
        final int updated;
        final long lastId;

        BatchResult(int processed, int updated, long lastId) {
            this.processed = processed;
            this.updated = updated;
            this.lastId = lastId;
        }
    }
}
