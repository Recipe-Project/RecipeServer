package com.recipe.app.src.recipe.infra.blog;

import com.recipe.app.src.common.infra.BaseRepositoryImpl;
import com.recipe.app.src.common.utils.SearchKeywordNormalizer.SearchQuery;
import com.recipe.app.src.recipe.domain.blog.BlogRecipe;

import jakarta.persistence.EntityManager;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static com.recipe.app.src.common.utils.QueryUtils.ifIdIsNotNullAndGreaterThanZero;
import static com.recipe.app.src.common.utils.QueryUtils.matchAgainst;
import static com.recipe.app.src.common.utils.QueryUtils.matchSearchQuery;
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
    public List<BlogRecipe> findByKeywordLimitOrderByPublishedAtDesc(SearchQuery query, Long lastBlogRecipeId, LocalDate lastBlogRecipePublishedAt, int size) {

        return queryFactory
                .selectFrom(blogRecipe)
                .where(
                        matchSearchQuery(blogRecipe.searchTokens, query),
                        ifIdIsNotNullAndGreaterThanZero((blogRecipeId, publishedAt) -> blogRecipe.publishedAt.lt(publishedAt)
                                        .or(blogRecipe.publishedAt.eq(publishedAt)
                                                .and(blogRecipe.blogRecipeId.lt(blogRecipeId))),
                                lastBlogRecipeId, lastBlogRecipePublishedAt)
                )
                .orderBy(blogRecipe.publishedAt.desc(), blogRecipe.blogRecipeId.desc())
                .limit(size)
                .fetch();
    }

    @Override
    public List<BlogRecipe> findByKeywordLimitOrderByBlogScrapCntDesc(SearchQuery query, Long lastBlogRecipeId, long lastBlogScrapCnt, int size) {

        return queryFactory
                .selectFrom(blogRecipe)
                .where(
                        matchSearchQuery(blogRecipe.searchTokens, query),
                        ifIdIsNotNullAndGreaterThanZero((blogRecipeId, blogScrapCnt) -> blogRecipe.scrapCnt.lt(blogScrapCnt)
                                        .or(blogRecipe.scrapCnt.eq(blogScrapCnt)
                                                .and(blogRecipe.blogRecipeId.lt(blogRecipeId))),
                                lastBlogRecipeId, lastBlogScrapCnt)
                )
                .orderBy(blogRecipe.scrapCnt.desc(), blogRecipe.blogRecipeId.desc())
                .limit(size)
                .fetch();
    }

    @Override
    public List<BlogRecipe> findByKeywordLimitOrderByBlogViewCntDesc(SearchQuery query, Long lastBlogRecipeId, long lastBlogViewCnt, int size) {

        return queryFactory
                .selectFrom(blogRecipe)
                .where(
                        matchSearchQuery(blogRecipe.searchTokens, query),
                        ifIdIsNotNullAndGreaterThanZero((blogRecipeId, blogViewCnt) -> blogRecipe.viewCnt.lt(blogViewCnt)
                                        .or(blogRecipe.viewCnt.eq(blogViewCnt)
                                                .and(blogRecipe.blogRecipeId.lt(blogRecipeId))),
                                lastBlogRecipeId, lastBlogViewCnt)
                )
                .orderBy(blogRecipe.viewCnt.desc(), blogRecipe.blogRecipeId.desc())
                .limit(size)
                .fetch();
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
