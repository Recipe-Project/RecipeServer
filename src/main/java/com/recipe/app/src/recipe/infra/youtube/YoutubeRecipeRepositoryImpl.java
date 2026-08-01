package com.recipe.app.src.recipe.infra.youtube;

import com.recipe.app.src.common.infra.BaseRepositoryImpl;
import com.recipe.app.src.common.utils.SearchKeywordNormalizer.SearchQuery;
import com.recipe.app.src.recipe.domain.youtube.YoutubeRecipe;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.NumberExpression;
import jakarta.persistence.EntityManager;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.function.Supplier;

import static com.recipe.app.src.common.utils.QueryUtils.ifIdIsNotNullAndGreaterThanZero;
import static com.recipe.app.src.common.utils.QueryUtils.matchAgainst;
import static com.recipe.app.src.common.utils.QueryUtils.matchSearchQuery;
import static com.recipe.app.src.common.utils.QueryUtils.relevanceScore;
import static com.recipe.app.src.common.utils.QueryUtils.titlePriorityScore;
import static com.recipe.app.src.recipe.domain.youtube.QYoutubeRecipe.youtubeRecipe;
import static com.recipe.app.src.recipe.domain.youtube.QYoutubeScrap.youtubeScrap;

public class YoutubeRecipeRepositoryImpl extends BaseRepositoryImpl implements YoutubeRecipeCustomRepository {

    public YoutubeRecipeRepositoryImpl(EntityManager em) {
        super(em);
    }

    @Override
    public Long countByKeyword(SearchQuery query) {

        return queryFactory
                .select(youtubeRecipe.count())
                .from(youtubeRecipe)
                .where(
                        matchSearchQuery(youtubeRecipe.searchTokens, query)
                )
                .fetchOne();
    }

    @Override
    public List<YoutubeRecipe> findByKeywordLimit(String keyword, int size) {

        return queryFactory
                .selectFrom(youtubeRecipe)
                .where(
                        matchAgainst(youtubeRecipe.searchTokens, keyword)
                )
                .limit(size)
                .fetch();
    }

    @Override
    public Double findRelevanceScoreByYoutubeRecipeId(SearchQuery query, Long youtubeRecipeId) {

        if (youtubeRecipeId == null || youtubeRecipeId <= 0 || !(query instanceof SearchQuery.BooleanQuery b)) {
            return null;
        }

        return queryFactory
                .select(titlePriorityScore(youtubeRecipe.titleSearchTokens, youtubeRecipe.searchTokens, b.query()))
                .from(youtubeRecipe)
                .where(youtubeRecipe.youtubeRecipeId.eq(youtubeRecipeId))
                .fetchOne();
    }

    @Override
    public List<YoutubeRecipe> findByKeywordLimitOrderByPostDateDesc(SearchQuery query, Long lastYoutubeRecipeId, Double lastRelevance, LocalDate lastYoutubeRecipePostDate, int size) {

        return queryFactory
                .selectFrom(youtubeRecipe)
                .where(
                        matchSearchQuery(youtubeRecipe.searchTokens, query),
                        relevanceCursor(query, lastYoutubeRecipeId, lastRelevance,
                                () -> youtubeRecipe.postDate.lt(lastYoutubeRecipePostDate), () -> youtubeRecipe.postDate.eq(lastYoutubeRecipePostDate))
                )
                .orderBy(relevanceOrder(query, youtubeRecipe.postDate.desc()))
                .limit(size)
                .fetch();
    }

    @Override
    public List<YoutubeRecipe> findByKeywordLimitOrderByYoutubeScrapCntDesc(SearchQuery query, Long lastYoutubeRecipeId, Double lastRelevance, long lastYoutubeScrapCnt, int size) {

        return queryFactory
                .selectFrom(youtubeRecipe)
                .where(
                        matchSearchQuery(youtubeRecipe.searchTokens, query),
                        relevanceCursor(query, lastYoutubeRecipeId, lastRelevance,
                                () -> youtubeRecipe.scrapCnt.lt(lastYoutubeScrapCnt), () -> youtubeRecipe.scrapCnt.eq(lastYoutubeScrapCnt))
                )
                .orderBy(relevanceOrder(query, youtubeRecipe.scrapCnt.desc()))
                .limit(size)
                .fetch();
    }

    @Override
    public List<YoutubeRecipe> findByKeywordLimitOrderByYoutubeViewCntDesc(SearchQuery query, Long lastYoutubeRecipeId, Double lastRelevance, long lastYoutubeViewCnt, int size) {

        return queryFactory
                .selectFrom(youtubeRecipe)
                .where(
                        matchSearchQuery(youtubeRecipe.searchTokens, query),
                        relevanceCursor(query, lastYoutubeRecipeId, lastRelevance,
                                () -> youtubeRecipe.viewCnt.lt(lastYoutubeViewCnt), () -> youtubeRecipe.viewCnt.eq(lastYoutubeViewCnt))
                )
                .orderBy(relevanceOrder(query, youtubeRecipe.viewCnt.desc()))
                .limit(size)
                .fetch();
    }

    // "검색어 일치율 대분류 → 소분류(secondary) → youtubeRecipeId" keyset 커서. (recipe 와 동일 패턴)
    private BooleanExpression relevanceCursor(SearchQuery query, Long lastYoutubeRecipeId, Double lastRelevance,
                                              Supplier<BooleanExpression> secondaryLt, Supplier<BooleanExpression> secondaryEq) {

        if (lastYoutubeRecipeId == null || lastYoutubeRecipeId <= 0) {
            return null;
        }

        BooleanExpression idLt = youtubeRecipe.youtubeRecipeId.lt(lastYoutubeRecipeId);

        if (query instanceof SearchQuery.BooleanQuery b && lastRelevance != null) {
            NumberExpression<Double> score = titlePriorityScore(youtubeRecipe.titleSearchTokens, youtubeRecipe.searchTokens, b.query());
            return score.lt(lastRelevance)
                    .or(score.eq(lastRelevance).and(secondaryLt.get()))
                    .or(score.eq(lastRelevance).and(secondaryEq.get()).and(idLt));
        }

        return secondaryLt.get().or(secondaryEq.get().and(idLt));
    }

    private OrderSpecifier<?>[] relevanceOrder(SearchQuery query, OrderSpecifier<?> secondary) {

        if (query instanceof SearchQuery.BooleanQuery b) {
            return new OrderSpecifier<?>[]{titlePriorityScore(youtubeRecipe.titleSearchTokens, youtubeRecipe.searchTokens, b.query()).desc(), secondary, youtubeRecipe.youtubeRecipeId.desc()};
        }

        return new OrderSpecifier<?>[]{secondary, youtubeRecipe.youtubeRecipeId.desc()};
    }

    @Override
    public List<YoutubeRecipe> findUserScrapYoutubeRecipesLimit(Long userId, Long lastYoutubeRecipeId, LocalDateTime lastScrapCreatedAt, int size) {

        return queryFactory
                .selectFrom(youtubeRecipe)
                .join(youtubeScrap).on(youtubeScrap.youtubeRecipeId.eq(youtubeRecipe.youtubeRecipeId), youtubeScrap.userId.eq(userId))
                .where(
                        ifIdIsNotNullAndGreaterThanZero((youtubeRecipeId, scrapCreatedAt) -> youtubeScrap.createdAt.lt(scrapCreatedAt)
                                        .or(youtubeScrap.createdAt.eq(scrapCreatedAt)
                                                .and(youtubeRecipe.youtubeRecipeId.lt(youtubeRecipeId))),
                                lastYoutubeRecipeId, lastScrapCreatedAt))
                .orderBy(youtubeScrap.createdAt.desc(), youtubeRecipe.youtubeRecipeId.desc())
                .limit(size)
                .fetch();
    }
}
