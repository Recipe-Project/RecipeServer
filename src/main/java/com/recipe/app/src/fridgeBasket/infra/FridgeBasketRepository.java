package com.recipe.app.src.fridgeBasket.infra;

import com.recipe.app.src.fridgeBasket.domain.FridgeBasket;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FridgeBasketRepository extends JpaRepository<FridgeBasket, Long> {

    List<FridgeBasket> findByUserId(Long userId);

    Optional<FridgeBasket> findByUserIdAndFridgeBasketId(Long userId, Long fridgeBasketId);

    long countByUserId(Long userId);

    Optional<FridgeBasket> findByIngredientIdAndUserId(Long ingredientId, Long userId);

    boolean existsByIngredientId(Long ingredientId);
}
