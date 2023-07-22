package com.recipe.app.src.fridgeBasket.mapper;

import com.recipe.app.src.fridgeBasket.domain.FridgeBasket;
import com.recipe.app.src.ingredient.domain.Ingredient;
import com.recipe.app.src.user.domain.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FridgeBasketRepository extends CrudRepository<FridgeBasket, Integer> {
    Optional<FridgeBasket> findByUserAndIngredientNameAndStatus(User user, String name, String status);

    Long countByUserAndStatus(User user, String status);

    List<FridgeBasket> findByUserAndStatus(User user, String status);

    List<FridgeBasket> findAllByUserAndStatusAndIngredientIn(User user, String status, List<Ingredient> ingredientList);

    List<FridgeBasket> findAllByUserAndStatusAndIngredientNameIn(User user, String status, List<String> ingredientNameList);
}
