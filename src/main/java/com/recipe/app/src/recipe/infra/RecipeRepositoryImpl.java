package com.recipe.app.src.recipe.infra;

import com.recipe.app.common.infra.BaseRepositoryImpl;
import com.recipe.app.src.recipe.domain.Recipe;
import jakarta.persistence.EntityManager;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static com.recipe.app.common.utils.QueryUtils.ifIdIsNotNullAndGreaterThanZero;
import static com.recipe.app.src.recipe.domain.QRecipe.recipe;
import static com.recipe.app.src.recipe.domain.QRecipeIngredient.recipeIngredient;
import static com.recipe.app.src.recipe.domain.QRecipeScrap.recipeScrap;

public class RecipeRepositoryImpl extends BaseRepositoryImpl implements RecipeCustomRepository {

    public RecipeRepositoryImpl(EntityManager em) {
        super(em);
    }

    @Override
    public Optional<Recipe> findRecipeDetail(Long recipeId, Long userId) {
        return Optional.ofNullable(queryFactory
                .selectFrom(recipe)
                .where(recipe.recipeId.eq(recipeId)
                        .and(recipe.hiddenYn.eq("N")
                                .or(recipe.hiddenYn.eq("Y").and(recipe.userId.eq(userId))))
                        )
                .fetchOne());
    }

    @Override
    public Long countByKeyword(String keyword) {
        return (long) queryFactory
                .select(recipe.recipeId, recipe.recipeNm, recipe.introduction)
                .from(recipe)
                .leftJoin(recipeIngredient).on(recipe.recipeId.eq(recipeIngredient.recipeId)
                        .and(recipeIngredient.ingredientName.contains(keyword)))
                .where(recipe.hiddenYn.eq("N"))
                .groupBy(recipe.recipeId)
                .having(recipe.recipeNm.contains(keyword)
                        .or(recipe.introduction.contains(keyword))
                        .or(recipeIngredient.count().gt(0)))
                .fetch().size();
    }

    @Override
    public List<Recipe> findByKeywordLimitOrderByCreatedAtDesc(String keyword, Long lastRecipeId, LocalDateTime lastCreatedAt, int size) {

        return queryFactory
                .selectFrom(recipe)
                .leftJoin(recipeIngredient).on(recipe.recipeId.eq(recipeIngredient.recipeId)
                        .and(recipeIngredient.ingredientName.contains(keyword)))
                .where(
                        ifIdIsNotNullAndGreaterThanZero((recipeId, createdAt) -> recipe.createdAt.lt(createdAt)
                                        .or(recipe.createdAt.eq(createdAt)
                                                .and(recipe.recipeId.lt(recipeId))),
                                lastRecipeId, lastCreatedAt),
                        recipe.hiddenYn.eq("N")
                )
                .groupBy(recipe.recipeId)
                .having(recipe.recipeNm.contains(keyword)
                        .or(recipe.introduction.contains(keyword))
                        .or(recipeIngredient.count().gt(0)))
                .orderBy(recipe.createdAt.desc(), recipe.recipeId.desc())
                .limit(size)
                .fetch();
    }

    @Override
    public List<Recipe> findByKeywordLimitOrderByRecipeScrapCntDesc(String keyword, Long lastRecipeId, long lastRecipeScrapCnt, int size) {

        return queryFactory
                .selectFrom(recipe)
                .leftJoin(recipeIngredient).on(recipe.recipeId.eq(recipeIngredient.recipeId)
                        .and(recipeIngredient.ingredientName.contains(keyword)))
                .where(recipe.hiddenYn.eq("N"),
                        ifIdIsNotNullAndGreaterThanZero((recipeId, recipeScrapCnt) -> recipe.scrapCnt.lt(recipeScrapCnt)
                                        .or(recipe.scrapCnt.eq(recipeScrapCnt)
                                                .and(recipe.recipeId.lt(recipeId))),
                                lastRecipeId, lastRecipeScrapCnt))
                .groupBy(recipe.recipeId)
                .having(recipe.recipeNm.contains(keyword)
                        .or(recipe.introduction.contains(keyword))
                        .or(recipeIngredient.count().gt(0)))
                .orderBy(recipe.scrapCnt.desc(), recipe.recipeId.desc())
                .limit(size)
                .fetch();
    }

    @Override
    public List<Recipe> findByKeywordLimitOrderByRecipeViewCntDesc(String keyword, Long lastRecipeId, long lastRecipeViewCnt, int size) {

        return queryFactory
                .selectFrom(recipe)
                .leftJoin(recipeIngredient).on(recipe.recipeId.eq(recipeIngredient.recipeId)
                        .and(recipeIngredient.ingredientName.contains(keyword)))
                .where(recipe.hiddenYn.eq("N"),
                        ifIdIsNotNullAndGreaterThanZero((recipeId, recipeViewCnt) -> recipe.viewCnt.lt(recipeViewCnt)
                                        .or(recipe.viewCnt.eq(recipeViewCnt)
                                                .and(recipe.recipeId.lt(recipeId))),
                                lastRecipeId, lastRecipeViewCnt))
                .groupBy(recipe.recipeId)
                .having(recipe.recipeNm.contains(keyword)
                        .or(recipe.introduction.contains(keyword))
                        .or(recipeIngredient.count().gt(0)))
                .orderBy(recipe.viewCnt.desc(), recipe.recipeId.desc())
                .limit(size)
                .fetch();
    }

    @Override
    public List<Recipe> findUserScrapRecipesLimit(Long userId, Long lastRecipeId, LocalDateTime lastScrapCreatedAt, int size) {
        return queryFactory
                .selectFrom(recipe)
                .join(recipeScrap).on(recipe.recipeId.eq(recipeScrap.recipeId), recipeScrap.userId.eq(userId))
                .where(
                        ifIdIsNotNullAndGreaterThanZero((recipeId, scrapCreatedAt) -> recipeScrap.createdAt.lt(scrapCreatedAt)
                                        .or(recipeScrap.createdAt.eq(scrapCreatedAt)
                                                .and(recipe.recipeId.lt(recipeId))),
                                lastRecipeId, lastScrapCreatedAt),
                        recipe.hiddenYn.eq("N")
                )
                .orderBy(recipeScrap.createdAt.desc(), recipe.recipeId.desc())
                .limit(size)
                .fetch();
    }

    @Override
    public List<Recipe> findLimitByUserId(Long userId, Long lastRecipeId, int size) {

        return queryFactory
                .selectFrom(recipe)
                .where(
                        recipe.userId.eq(userId),
                        ifIdIsNotNullAndGreaterThanZero(recipe.recipeId::lt, lastRecipeId)
                )
                .orderBy(recipe.recipeId.desc())
                .limit(size)
                .fetch();
    }

    @Override
    public List<Recipe> findRecipesInFridge(Collection<String> ingredientNames) {

        return queryFactory
                .selectFrom(recipe)
                .join(recipeIngredient).on(recipe.recipeId.eq(recipeIngredient.recipeId))
                .where(
                        (recipeIngredient.ingredientName.in(ingredientNames)),
                        recipe.hiddenYn.eq("N")
                )
                .groupBy(recipe.recipeId)
                .fetch();
    }
}
