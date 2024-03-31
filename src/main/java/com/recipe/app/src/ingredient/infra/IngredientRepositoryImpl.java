package com.recipe.app.src.ingredient.infra;

import com.recipe.app.common.infra.BaseRepositoryImpl;
import com.recipe.app.src.ingredient.domain.Ingredient;
import com.recipe.app.src.ingredient.domain.QIngredient;

import javax.persistence.EntityManager;
import java.util.List;

import static com.recipe.app.src.ingredient.domain.QIngredient.ingredient;

public class IngredientRepositoryImpl extends BaseRepositoryImpl implements IngredientCustomRepository {

    public IngredientRepositoryImpl(EntityManager em) {
        super(em);
    }

    @Override
    public List<Ingredient> findDefaultIngredientsByKeyword(String keyword) {

        return queryFactory
                .selectFrom(ingredient)
                .where(
                        ingredient.userId.isNull(),
                        ingredient.ingredientName.contains(keyword),
                        ingredient.defaultYn.eq("Y"),
                        ingredient.hiddenYn.eq("N")
                )
                .fetch();
    }

    @Override
    public List<Ingredient> findDefaultIngredients() {

        return queryFactory
                .selectFrom(ingredient)
                .where(
                        ingredient.userId.isNull(),
                        ingredient.defaultYn.eq("Y"),
                        ingredient.hiddenYn.eq("N")
                )
                .fetch();
    }
}
