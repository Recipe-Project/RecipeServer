package com.recipe.app.src.ingredient.infra;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.recipe.app.src.common.infra.BaseRepositoryImpl;
import com.recipe.app.src.ingredient.domain.Ingredient;
import jakarta.persistence.EntityManager;

import java.util.List;
import java.util.function.Function;

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
                        ifNotNull(ingredient.ingredientName::contains, keyword)
                )
                .orderBy(ingredient.ingredientId.asc())
                .fetch();
    }

    private <T> BooleanExpression ifNotNull(Function<T, BooleanExpression> function, T object) {
        return object != null ? function.apply(object) : null;
    }
}
