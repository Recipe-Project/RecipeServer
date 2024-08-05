package com.recipe.app.src.ingredient.infra;

import com.recipe.app.src.common.infra.BaseRepositoryImpl;
import com.recipe.app.src.ingredient.domain.Ingredient;
import jakarta.persistence.EntityManager;

import java.util.List;

import static com.recipe.app.src.ingredient.domain.QIngredient.ingredient;

public class IngredientRepositoryImpl extends BaseRepositoryImpl implements IngredientCustomRepository {

    public IngredientRepositoryImpl(EntityManager em) {
        super(em);
    }

    @Override
    public List<Ingredient> findDefaultIngredientsByKeyword(Long userId, String keyword) {

        return queryFactory
                .selectFrom(ingredient)
                .where(
                        (ingredient.ingredientCategoryId.ne(7L)
                                .and(ingredient.userId.isNull().or(ingredient.userId.eq(userId))))
                                .or(ingredient.ingredientCategoryId.eq(7L)
                                        .and(ingredient.userId.eq(userId))),
                        ingredient.ingredientName.contains(keyword)
                )
                .orderBy(ingredient.ingredientId.asc())
                .fetch();
    }

    @Override
    public List<Ingredient> findDefaultIngredients(Long userId) {

        return queryFactory
                .selectFrom(ingredient)
                .where(
                        (ingredient.ingredientCategoryId.ne(7L)
                                .and(ingredient.userId.isNull().or(ingredient.userId.eq(userId))))
                                .or(ingredient.ingredientCategoryId.eq(7L)
                                        .and(ingredient.userId.eq(userId)))
                )
                .orderBy(ingredient.ingredientId.asc())
                .fetch();
    }
}
