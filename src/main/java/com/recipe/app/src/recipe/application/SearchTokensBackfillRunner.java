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

@Slf4j
@Component
@ConditionalOnProperty(value = "recipe.migration.search-tokens.enabled", havingValue = "true")
public class SearchTokensBackfillRunner implements ApplicationRunner {

    private static final int BATCH_SIZE = 500;

    @PersistenceContext
    private EntityManager em;

    private final TransactionTemplate transactionTemplate;

    public SearchTokensBackfillRunner(PlatformTransactionManager transactionManager) {
        this.transactionTemplate = new TransactionTemplate(transactionManager);
    }

    @Override
    public void run(ApplicationArguments args) {

        log.info("SearchTokens backfill started");
        backfill("Recipe", "recipeId", new String[]{"recipeNm", "introduction"});
        backfill("RecipeIngredient", "recipeIngredientId", new String[]{"ingredientName"});
        backfill("BlogRecipe", "blogRecipeId", new String[]{"title", "description"});
        backfill("YoutubeRecipe", "youtubeRecipeId", new String[]{"title", "description"});
        log.info("SearchTokens backfill done");
    }

    private void backfill(String table, String pk, String[] sourceColumns) {

        log.info("[{}] backfill started", table);
        long lastId = 0L;
        long total = 0L;
        while (true) {
            long startId = lastId;
            BatchResult r = transactionTemplate.execute(status -> processBatch(table, pk, sourceColumns, startId));
            if (r == null || r.processed == 0) break;
            lastId = r.lastId;
            total += r.updated;
            log.info("[{}] progress lastId={} updatedSoFar={}", table, lastId, total);
        }
        log.info("[{}] backfill done. updated={}", table, total);
    }

    private BatchResult processBatch(String table, String pk, String[] sourceColumns, long startId) {

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

        String updateSql = "UPDATE " + table + " SET searchTokens = :tokens WHERE " + pk + " = :id";

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
            String tokens = KoreanTokenizer.tokenize(text.toString());
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
