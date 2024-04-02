package com.recipe.app.src.recipe.infra.blog;

import com.recipe.app.common.infra.BaseRepositoryImpl;
import com.recipe.app.src.recipe.domain.blog.BlogRecipe;

import javax.persistence.EntityManager;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static com.recipe.app.src.recipe.domain.blog.QBlogRecipe.blogRecipe;
import static com.recipe.app.src.recipe.domain.blog.QBlogScrap.blogScrap;
import static com.recipe.app.src.recipe.domain.blog.QBlogView.blogView;

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
                        blogRecipe.publishedAt.lt(lastBlogRecipePublishedAt)
                                .or(blogRecipe.publishedAt.eq(lastBlogRecipePublishedAt)
                                        .and(blogRecipe.blogRecipeId.lt(lastBlogRecipeId)))
                )
                .orderBy(blogRecipe.publishedAt.desc(), blogRecipe.blogRecipeId.desc())
                .limit(size)
                .fetch();
    }

    @Override
    public List<BlogRecipe> findByKeywordLimitOrderByBlogScrapCntDesc(String keyword, Long lastBlogRecipeId, long lastBlogScrapCnt, int size) {

        return queryFactory
                .selectFrom(blogRecipe)
                .leftJoin(blogScrap).on(blogRecipe.blogRecipeId.eq(blogScrap.blogRecipeId))
                .where(
                        blogRecipe.title.contains(keyword)
                                .or(blogRecipe.description.contains(keyword))
                )
                .groupBy(blogRecipe.blogRecipeId)
                .having(blogScrap.count().lt(lastBlogScrapCnt)
                        .or(blogScrap.count().eq(lastBlogScrapCnt)
                                .and(blogRecipe.blogRecipeId.lt(lastBlogRecipeId))))
                .orderBy(blogScrap.count().desc(), blogRecipe.blogRecipeId.desc())
                .limit(size)
                .fetch();
    }

    @Override
    public List<BlogRecipe> findByKeywordLimitOrderByBlogViewCntDesc(String keyword, Long lastBlogRecipeId, long lastBlogViewCnt, int size) {

        return queryFactory
                .selectFrom(blogRecipe)
                .leftJoin(blogView).on(blogRecipe.blogRecipeId.eq(blogView.blogRecipeId))
                .where(
                        blogRecipe.title.contains(keyword)
                                .or(blogRecipe.description.contains(keyword))
                )
                .groupBy(blogRecipe.blogRecipeId)
                .having(blogView.count().lt(lastBlogViewCnt)
                        .or(blogView.count().eq(lastBlogViewCnt)
                                .and(blogRecipe.blogRecipeId.lt(lastBlogRecipeId))))
                .orderBy(blogView.count().desc(), blogRecipe.blogRecipeId.desc())
                .limit(size)
                .fetch();
    }

    @Override
    public List<BlogRecipe> findUserScrapBlogRecipesLimit(Long userId, Long lastBlogRecipeId, LocalDateTime scrapCreatedAt, int size) {

        return queryFactory
                .selectFrom(blogRecipe)
                .join(blogScrap).on(blogRecipe.blogRecipeId.eq(blogScrap.blogRecipeId), blogScrap.userId.eq(userId))
                .where(
                        blogScrap.createdAt.lt(scrapCreatedAt)
                                .or(blogScrap.createdAt.eq(scrapCreatedAt)
                                        .and(blogRecipe.blogRecipeId.lt(lastBlogRecipeId)))
                )
                .orderBy(blogScrap.createdAt.desc(), blogRecipe.blogRecipeId.desc())
                .limit(size)
                .fetch();
    }
}
