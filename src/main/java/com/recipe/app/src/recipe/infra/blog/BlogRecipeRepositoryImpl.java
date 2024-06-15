package com.recipe.app.src.recipe.infra.blog;

import com.recipe.app.common.infra.BaseRepositoryImpl;
import com.recipe.app.src.recipe.domain.blog.BlogRecipe;

import javax.persistence.EntityManager;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static com.recipe.app.common.utils.QueryUtils.*;
import static com.recipe.app.src.recipe.domain.blog.QBlogRecipe.blogRecipe;
import static com.recipe.app.src.recipe.domain.blog.QBlogScrap.blogScrap;

public class BlogRecipeRepositoryImpl extends BaseRepositoryImpl implements BlogRecipeCustomRepository {

    public BlogRecipeRepositoryImpl(EntityManager em) {
        super(em);
    }

    @Override
    public Long countByKeyword(String keyword) {

        return queryFactory
                .select(blogRecipe.count())
                .from(blogRecipe)
                .where(
                        blogRecipe.title.contains(keyword)
                                .or(blogRecipe.description.contains(keyword))
                )
                .fetchOne();
    }

    @Override
    public List<BlogRecipe> findByKeywordLimitOrderByPublishedAtDesc(String keyword, Long lastBlogRecipeId, LocalDate lastBlogRecipePublishedAt, int size) {

        return queryFactory
                .selectFrom(blogRecipe)
                .where(
                        blogRecipe.title.contains(keyword)
                                .or(blogRecipe.description.contains(keyword)),
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
    public List<BlogRecipe> findByKeywordLimitOrderByBlogScrapCntDesc(String keyword, Long lastBlogRecipeId, long lastBlogScrapCnt, int size) {

        return queryFactory
                .selectFrom(blogRecipe)
                .where(
                        blogRecipe.title.contains(keyword)
                                .or(blogRecipe.description.contains(keyword)),
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
    public List<BlogRecipe> findByKeywordLimitOrderByBlogViewCntDesc(String keyword, Long lastBlogRecipeId, long lastBlogViewCnt, int size) {

        return queryFactory
                .selectFrom(blogRecipe)
                .where(
                        blogRecipe.title.contains(keyword)
                                .or(blogRecipe.description.contains(keyword)),
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
