package com.recipe.app.src.recipe.infra;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.JPAExpressions;
import com.recipe.app.common.infra.BaseRepositoryImpl;
import com.recipe.app.src.recipe.domain.Recipe;
import com.recipe.app.src.recipe.domain.RecipeWithRate;

import javax.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.List;

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
        return queryFactory
                .select(recipe.count())
                .from(recipe)
                .join(recipeIngredient).on(recipe.recipeId.eq(recipeIngredient.recipeId))
                .join(ingredient).on(ingredient.ingredientId.eq(recipeIngredient.ingredientId))
                .where(
                        recipe.recipeNm.contains(keyword)
                                .or(recipe.introduction.contains(keyword))
                                .or(ingredient.ingredientName.contains(keyword)),
                        recipe.hiddenYn.eq("N")
                )
                .groupBy(recipe.recipeId)
                .fetchOne();
    }

    @Override
    public List<Recipe> findByKeywordLimitOrderByCreatedAtDesc(String keyword, Long lastRecipeId, LocalDateTime createdAt, int size) {

        return queryFactory
                .selectFrom(recipe)
                .join(recipeIngredient).on(recipe.recipeId.eq(recipeIngredient.recipeId))
                .join(ingredient).on(ingredient.ingredientId.eq(recipeIngredient.ingredientId))
                .where(
                        recipe.recipeNm.contains(keyword)
                                .or(recipe.introduction.contains(keyword))
                                .or(ingredient.ingredientName.contains(keyword)),
                        recipe.recipeId.lt(lastRecipeId)
                                .or(recipe.createdAt.loe(createdAt)),
                        recipe.hiddenYn.eq("N")
                )
                .groupBy(recipe.recipeId)
                .orderBy(recipe.createdAt.desc(), ingredient.count().desc(), recipe.recipeId.desc())
                .limit(size)
                .fetch();
    }

    @Override
    public List<Recipe> findByKeywordLimitOrderByRecipeScrapCntDesc(String keyword, Long lastRecipeId, long recipeScrapCnt, int size) {

        return queryFactory
                .selectFrom(recipe)
                .join(recipeIngredient).on(recipe.recipeId.eq(recipeIngredient.recipeId))
                .join(ingredient).on(ingredient.ingredientId.eq(recipeIngredient.ingredientId))
                .join(recipeScrap).on(recipeScrap.recipeId.eq(recipe.recipeId))
                .where(
                        recipe.recipeNm.contains(keyword)
                                .or(recipe.introduction.contains(keyword))
                                .or(ingredient.ingredientName.contains(keyword)),
                        recipe.recipeId.lt(lastRecipeId)
                                .or(recipeScrap.count().loe(recipeScrapCnt)),
                        recipe.hiddenYn.eq("N")
                )
                .groupBy(recipe.recipeId)
                .orderBy(recipeScrap.count().desc(), ingredient.count().desc(), recipe.recipeId.desc())
                .limit(size)
                .fetch();
    }

    @Override
    public List<Recipe> findByKeywordLimitOrderByRecipeViewCntDesc(String keyword, Long lastRecipeId, long recipeViewCnt, int size) {

        return queryFactory
                .selectFrom(recipe)
                .join(recipeIngredient).on(recipe.recipeId.eq(recipeIngredient.recipeId))
                .join(ingredient).on(ingredient.ingredientId.eq(recipeIngredient.ingredientId))
                .join(recipeView).on(recipeView.recipeId.eq(recipe.recipeId))
                .where(
                        recipe.recipeNm.contains(keyword)
                                .or(recipe.introduction.contains(keyword))
                                .or(ingredient.ingredientName.contains(keyword)),
                        recipe.recipeId.lt(lastRecipeId)
                                .or(recipeView.count().loe(recipeViewCnt)),
                        recipe.hiddenYn.eq("N")
                )
                .groupBy(recipe.recipeId)
                .orderBy(recipeView.count().desc(), ingredient.count().desc(), recipe.recipeId.desc())
                .limit(size)
                .fetch();
    }

    @Override
    public List<Recipe> findUserScrapRecipesLimit(Long userId, Long lastRecipeId, LocalDateTime scrapCreatedAt, int size) {
        return queryFactory
                .selectFrom(recipe)
                .join(recipeScrap).on(recipe.recipeId.eq(recipeScrap.recipeId), recipeScrap.userId.eq(userId))
                .where(
                        recipe.recipeId.lt(lastRecipeId)
                                .or(recipeScrap.createdAt.loe(scrapCreatedAt)),
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
                        recipe.recipeId.lt(lastRecipeId)
                )
                .orderBy(recipe.recipeId.desc())
                .limit(size)
                .fetch();
    }

    @Override
    public Long countRecipesWithRate(List<Long> ingredientIds, List<String> ingredientNames) {

        return queryFactory
                .select(recipe.count())
                .from(recipe)
                .join(recipeIngredient).on(recipe.recipeId.eq(recipeIngredient.recipeId))
                .join(ingredient).on(ingredient.ingredientId.eq(recipeIngredient.ingredientId))
                .where(
                        ingredient.ingredientId.in(ingredientIds)
                                .or(ingredient.ingredientName.in(ingredientNames)),
                        recipe.hiddenYn.eq("N")
                )
                .groupBy(recipe.recipeId)
                .fetchOne();
    }

    @Override
    public Long countRecipeRate(List<Long> ingredientIds, List<String> ingredientNames, Long recipeId) {

        return queryFactory
                .select(getMatchRatePath())
                .from(recipe)
                .join(recipeIngredient).on(recipe.recipeId.eq(recipeIngredient.recipeId))
                .join(ingredient).on(ingredient.ingredientId.eq(recipeIngredient.ingredientId))
                .where(
                        recipe.recipeId.eq(recipeId),
                        ingredient.ingredientId.in(ingredientIds)
                                .or(ingredient.ingredientName.in(ingredientNames)),
                        recipe.hiddenYn.eq("N")
                )
                .fetchOne();
    }

    @Override
    public List<RecipeWithRate> findRecipesWithRateLimitOrderByFridgeIngredientCntDesc(List<Long> ingredientIds, List<String> ingredientNames, Long lastRecipeId, Long matchRate, int size) {

        NumberExpression<Long> matchRatePath = getMatchRatePath();

        return queryFactory
                .select(Projections.fields(RecipeWithRate.class, recipe, matchRatePath.as("matchRate")))
                .from(recipe)
                .join(recipeIngredient).on(recipe.recipeId.eq(recipeIngredient.recipeId))
                .join(ingredient).on(ingredient.ingredientId.eq(recipeIngredient.ingredientId))
                .where(
                        ingredient.ingredientId.in(ingredientIds)
                                .or(ingredient.ingredientName.in(ingredientNames)),
                        recipe.recipeId.lt(lastRecipeId)
                                .or(matchRatePath.loe(matchRate)),
                        recipe.hiddenYn.eq("N")
                )
                .groupBy(recipe.recipeId)
                .orderBy(matchRatePath.desc(), recipe.recipeId.desc())
                .limit(size)
                .fetch();

    }

    private NumberExpression<Long> getMatchRatePath() {

        return recipeIngredient.count().divide(JPAExpressions.select(recipeIngredient.count())
                .from(recipeIngredient)
                .where(recipeIngredient.recipeId.eq(recipe.recipeId))
        );
    }
}
