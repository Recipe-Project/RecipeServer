package com.recipe.app.src.fridgeBasket.application.port;

import com.recipe.app.src.fridgeBasket.domain.FridgeBasket;
import com.recipe.app.src.ingredient.domain.Ingredient;
import com.recipe.app.src.user.domain.User;

import java.util.List;
import java.util.Optional;

public interface FridgeBasketRepository {
    List<FridgeBasket> saveAll(List<FridgeBasket> fridgeBaskets);

    List<FridgeBasket> findByUser(User user);

    List<FridgeBasket> findByUserAndFridgeBasketIdIn(User user, List<Long> fridgeBasketIds);

    void deleteAll(List<FridgeBasket> fridgeBaskets);

    long countByUser(User user);

    FridgeBasket save(FridgeBasket fridgeBasket);

    List<FridgeBasket> findByFridgeBasketIdIn(List<Long> fridgeBasketIds);

    Optional<FridgeBasket> findByIngredientAndUser(Ingredient ingredient, User user);
}
