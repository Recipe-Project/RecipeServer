package com.recipe.app.src.fridgeBasket;

import com.recipe.app.src.fridgeBasket.models.FridgeBasket;
import com.recipe.app.src.ingredient.models.Ingredient;
import com.recipe.app.src.user.models.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FridgeBasketRepository extends CrudRepository<FridgeBasket, Integer> {
    FridgeBasket findByUserAndIngredientNameAndStatus(User user, String name, String status);
    Long countByUserAndStatus(User user, String status);
    List<FridgeBasket> findByUserAndStatus(User user, String status);
    Boolean existsByUserAndIngredientNameAndStatus(User user, String ingredientName, String active);

    List<FridgeBasket> findAllByUserAndStatusAndIngredientIn(User user, String status, List<Ingredient> ingredientList);
    List<FridgeBasket> findAllByUserAndStatusAndIngredientNameIn(User user, String status, List<String> ingredientNameList);
}
