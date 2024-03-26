package com.recipe.app.src.fridgeBasket.application.port;

import com.recipe.app.src.ingredient.domain.Ingredient;
import com.recipe.app.src.user.domain.User;

import java.util.List;
import java.util.Optional;

public interface FridgeBasketRepository {
    void saveAll(List<FridgeBasket> fridgeBaskets);

    List<FridgeBasket> findByUser(User user);

    Optional<FridgeBasket> findByUserAndFridgeBasketId(User user, Long fridgeBasketId);

    void delete(FridgeBasket fridgeBasket);

    long countByUser(User user);

    FridgeBasket save(FridgeBasket fridgeBasket);

    Optional<FridgeBasket> findByIngredientAndUser(Ingredient ingredient, User user);

    void deleteAll(List<FridgeBasket> fridgeBaskets);
}
