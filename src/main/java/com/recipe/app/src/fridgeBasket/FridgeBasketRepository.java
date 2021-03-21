package com.recipe.app.src.fridgeBasket;

import com.recipe.app.src.fridgeBasket.models.FridgeBasket;
import com.recipe.app.src.ingredient.models.Ingredient;
import com.recipe.app.src.ingredientCategory.models.IngredientCategory;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FridgeBasketRepository extends CrudRepository<FridgeBasket, Integer> {
    FridgeBasket findByIngredientNameAndStatus(String name, String status);
}
