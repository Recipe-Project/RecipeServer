package com.recipe.app.src.recipe.infra;

import com.recipe.app.common.infra.BaseRepositoryImpl;
import com.recipe.app.src.recipe.domain.Recipe;

import javax.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

import static com.recipe.app.common.utils.QueryUtils.ifIdIsNotNullAndGreaterThanZero;
import static com.recipe.app.src.ingredient.domain.QIngredient.ingredient;
import static com.recipe.app.src.recipe.domain.QRecipe.recipe;
import static com.recipe.app.src.recipe.domain.QRecipeIngredient.recipeIngredient;
import static com.recipe.app.src.recipe.domain.QRecipeScrap.recipeScrap;
import static com.recipe.app.src.recipe.domain.QRecipeView.recipeView;

public class RecipeRepositoryImpl extends BaseRepositoryImpl implements RecipeCustomRepository {

    public RecipeRepositoryImpl(EntityManager em) {
        super(em);
    }

    @Override
    public Long countByKeyword(String keyword) {
        return (long) queryFactory
                .select(recipe.recipeId, recipe.recipeNm, recipe.introduction)
                .from(recipe)
                .leftJoin(recipeIngredient).on(recipe.recipeId.eq(recipeIngredient.recipeId))
                .leftJoin(ingredient).on(ingredient.ingredientId.eq(recipeIngredient.ingredientId), ingredient.ingredientName.contains(keyword))
                .where(recipe.hiddenYn.eq("N"))
                .groupBy(recipe.recipeId)
                .having(recipe.recipeNm.contains(keyword)
                        .or(recipe.introduction.contains(keyword))
                        .or(ingredient.count().gt(0)))
                .fetch().size();
    }

    @Override
    public List<Recipe> findByKeywordLimitOrderByCreatedAtDesc(String keyword, Long lastRecipeId, LocalDateTime lastCreatedAt, int size) {

        return queryFactory
                .selectFrom(recipe)
                .leftJoin(recipeIngredient).on(recipe.recipeId.eq(recipeIngredient.recipeId))
                .leftJoin(ingredient).on(ingredient.ingredientId.eq(recipeIngredient.ingredientId), ingredient.ingredientName.contains(keyword))
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
                        .or(ingredient.count().gt(0)))
                .orderBy(recipe.createdAt.desc(), recipe.recipeId.desc())
                .limit(size)
                .fetch();
    }

    @Override
    public List<Recipe> findByKeywordLimitOrderByRecipeScrapCntDesc(String keyword, Long lastRecipeId, long lastRecipeScrapCnt, int size) {

        return queryFactory
                .selectFrom(recipe)
                .leftJoin(recipeIngredient).on(recipe.recipeId.eq(recipeIngredient.recipeId))
                .leftJoin(ingredient).on(ingredient.ingredientId.eq(recipeIngredient.ingredientId), ingredient.ingredientName.contains(keyword))
                .leftJoin(recipeScrap).on(recipeScrap.recipeId.eq(recipe.recipeId))
                .where(recipe.hiddenYn.eq("N"))
                .groupBy(recipe.recipeId)
                .having(recipe.recipeNm.contains(keyword)
                                .or(recipe.introduction.contains(keyword))
                                .or(ingredient.count().gt(0)),
                        ifIdIsNotNullAndGreaterThanZero((recipeId, recipeScrapCnt) -> recipeScrap.count().lt(recipeScrapCnt)
                                        .or(recipeScrap.count().eq(recipeScrapCnt)
                                                .and(recipe.recipeId.lt(recipeId))),
                                lastRecipeScrapCnt, lastRecipeScrapCnt))
                .orderBy(recipeScrap.count().desc(), recipe.recipeId.desc())
                .limit(size)
                .fetch();
    }

    @Override
    public List<Recipe> findByKeywordLimitOrderByRecipeViewCntDesc(String keyword, Long lastRecipeId, long lastRecipeViewCnt, int size) {

        return queryFactory
                .selectFrom(recipe)
                .leftJoin(recipeIngredient).on(recipe.recipeId.eq(recipeIngredient.recipeId))
                .leftJoin(ingredient).on(ingredient.ingredientId.eq(recipeIngredient.ingredientId), ingredient.ingredientName.contains(keyword))
                .leftJoin(recipeView).on(recipeView.recipeId.eq(recipe.recipeId))
                .where(recipe.hiddenYn.eq("N"))
                .groupBy(recipe.recipeId)
                .having(recipe.recipeNm.contains(keyword)
                                .or(recipe.introduction.contains(keyword))
                                .or(ingredient.count().gt(0)),
                        ifIdIsNotNullAndGreaterThanZero((recipeId, recipeViewCnt) -> recipeView.count().lt(recipeViewCnt)
                                        .or(recipeView.count().eq(recipeViewCnt)
                                                .and(recipe.recipeId.lt(recipeId))),
                                lastRecipeId, lastRecipeViewCnt))
                .orderBy(recipeView.count().desc(), recipe.recipeId.desc())
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
    public List<Recipe> findRecipesInFridge(Collection<Long> ingredientIds, Collection<String> ingredientNames) {

        return queryFactory
                .selectFrom(recipe)
                .join(recipeIngredient).on(recipe.recipeId.eq(recipeIngredient.recipeId))
                .join(ingredient).on(ingredient.ingredientId.eq(recipeIngredient.ingredientId))
                .where(
                        ingredient.ingredientId.in(ingredientIds)
                                .or(ingredient.ingredientName.in(ingredientNames)),
                        recipe.hiddenYn.eq("N")
                )
                .groupBy(recipe.recipeId)
                .fetch();
    }
}
