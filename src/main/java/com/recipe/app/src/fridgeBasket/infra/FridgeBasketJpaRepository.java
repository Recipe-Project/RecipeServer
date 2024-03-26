package com.recipe.app.src.fridgeBasket.infra;

import com.recipe.app.src.fridgeBasket.domain.FridgeBasket;
import com.recipe.app.src.ingredient.infra.IngredientEntity;
import com.recipe.app.src.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FridgeBasketJpaRepository extends JpaRepository<FridgeBasket, Long> {

    List<FridgeBasket> findByUserId(Long userId);

    Optional<FridgeBasket> findByUserIdAndFridgeBasketId(Long userId, Long fridgeBasketId);

    long countByUserId(Long userId);

    Optional<FridgeBasket> findByIngredientIdAndUserId(Long ingredientId, Long userId);
}
