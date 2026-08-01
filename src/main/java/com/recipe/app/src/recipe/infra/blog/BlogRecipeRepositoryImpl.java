package com.recipe.app.src.recipe.infra.blog;

import com.recipe.app.src.common.infra.BaseRepositoryImpl;
import com.recipe.app.src.common.utils.SearchKeywordNormalizer.SearchQuery;
import com.recipe.app.src.recipe.domain.blog.BlogRecipe;
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
import static com.recipe.app.src.recipe.domain.blog.QBlogRecipe.blogRecipe;
import static com.recipe.app.src.recipe.domain.blog.QBlogScrap.blogScrap;

public class BlogRecipeRepositoryImpl extends BaseRepositoryImpl implements BlogRecipeCustomRepository {

    public BlogRecipeRepositoryImpl(EntityManager em) {
        super(em);
    }

    @Override
    public Long countByKeyword(SearchQuery query) {

        return queryFactory
                .select(blogRecipe.count())
                .from(blogRecipe)
                .where(
                        matchSearchQuery(blogRecipe.searchTokens, query)
                )
                .fetchOne();
    }

    @Override
    public List<BlogRecipe> findByKeywordLimit(String keyword, int size) {

        return queryFactory
                .selectFrom(blogRecipe)
                .where(
                        matchAgainst(blogRecipe.searchTokens, keyword)
                )
                .limit(size)
                .fetch();
    }

    @Override
    public Double findRelevanceScoreByBlogRecipeId(SearchQuery query, Long blogRecipeId) {

        if (blogRecipeId == null || blogRecipeId <= 0 || !(query instanceof SearchQuery.BooleanQuery b)) {
            return null;
        }

        return queryFactory
                .select(relevanceScore(blogRecipe.searchTokens, b.query()))
                .from(blogRecipe)
                .where(blogRecipe.blogRecipeId.eq(blogRecipeId))
                .fetchOne();
    }

    @Override
    public List<BlogRecipe> findByKeywordLimitOrderByPublishedAtDesc(SearchQuery query, Long lastBlogRecipeId, Double lastRelevance, LocalDate lastBlogRecipePublishedAt, int size) {

        return queryFactory
                .selectFrom(blogRecipe)
                .where(
                        matchSearchQuery(blogRecipe.searchTokens, query),
                        relevanceCursor(query, lastBlogRecipeId, lastRelevance,
                                () -> blogRecipe.publishedAt.lt(lastBlogRecipePublishedAt), () -> blogRecipe.publishedAt.eq(lastBlogRecipePublishedAt))
                )
                .orderBy(relevanceOrder(query, blogRecipe.publishedAt.desc()))
                .limit(size)
                .fetch();
    }

    @Override
    public List<BlogRecipe> findByKeywordLimitOrderByBlogScrapCntDesc(SearchQuery query, Long lastBlogRecipeId, Double lastRelevance, long lastBlogScrapCnt, int size) {

        return queryFactory
                .selectFrom(blogRecipe)
                .where(
                        matchSearchQuery(blogRecipe.searchTokens, query),
                        relevanceCursor(query, lastBlogRecipeId, lastRelevance,
                                () -> blogRecipe.scrapCnt.lt(lastBlogScrapCnt), () -> blogRecipe.scrapCnt.eq(lastBlogScrapCnt))
                )
                .orderBy(relevanceOrder(query, blogRecipe.scrapCnt.desc()))
                .limit(size)
                .fetch();
    }

    @Override
    public List<BlogRecipe> findByKeywordLimitOrderByBlogViewCntDesc(SearchQuery query, Long lastBlogRecipeId, Double lastRelevance, long lastBlogViewCnt, int size) {

        return queryFactory
                .selectFrom(blogRecipe)
                .where(
                        matchSearchQuery(blogRecipe.searchTokens, query),
                        relevanceCursor(query, lastBlogRecipeId, lastRelevance,
                                () -> blogRecipe.viewCnt.lt(lastBlogViewCnt), () -> blogRecipe.viewCnt.eq(lastBlogViewCnt))
                )
                .orderBy(relevanceOrder(query, blogRecipe.viewCnt.desc()))
                .limit(size)
                .fetch();
    }

    // "검색어 일치율 대분류 → 소분류(secondary) → blogRecipeId" keyset 커서. (recipe 와 동일 패턴)
    private BooleanExpression relevanceCursor(SearchQuery query, Long lastBlogRecipeId, Double lastRelevance,
                                              Supplier<BooleanExpression> secondaryLt, Supplier<BooleanExpression> secondaryEq) {

        if (lastBlogRecipeId == null || lastBlogRecipeId <= 0) {
            return null;
        }

        BooleanExpression idLt = blogRecipe.blogRecipeId.lt(lastBlogRecipeId);

        if (query instanceof SearchQuery.BooleanQuery b && lastRelevance != null) {
            NumberExpression<Double> score = relevanceScore(blogRecipe.searchTokens, b.query());
            return score.lt(lastRelevance)
                    .or(score.eq(lastRelevance).and(secondaryLt.get()))
                    .or(score.eq(lastRelevance).and(secondaryEq.get()).and(idLt));
        }

        return secondaryLt.get().or(secondaryEq.get().and(idLt));
    }

    private OrderSpecifier<?>[] relevanceOrder(SearchQuery query, OrderSpecifier<?> secondary) {

        if (query instanceof SearchQuery.BooleanQuery b) {
            return new OrderSpecifier<?>[]{relevanceScore(blogRecipe.searchTokens, b.query()).desc(), secondary, blogRecipe.blogRecipeId.desc()};
        }

        return new OrderSpecifier<?>[]{secondary, blogRecipe.blogRecipeId.desc()};
    }

    @Override
    public List<BlogRecipe> findUserScrapBlogRecipesLimit(Long userId, Long lastBlogRecipeId, LocalDateTime lastScrapCreatedAt, int size) {

        return queryFactory
                .selectFrom(blogRecipe)
                .join(blogScrap).on(blogRecipe.blogRecipeId.eq(blogScrap.blogRecipeId), blogScrap.userId.eq(userId))
                .where(
                        ifIdIsNotNullAndGreaterThanZero((blogRecipeId, scrapCreatedAt) -> blogScrap.createdAt.lt(scrapCreatedAt)
                                        .or(blogScrap.createdAt.eq(scrapCreatedAt)
                                                .and(blogRecipe.blogRecipeId.lt(blogRecipeId))),
                                lastBlogRecipeId, lastScrapCreatedAt)
                )
                .orderBy(blogScrap.createdAt.desc(), blogRecipe.blogRecipeId.desc())
                .limit(size)
                .fetch();
    }
}
