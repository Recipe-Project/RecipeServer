package com.recipe.app.src.fridgeBasket.infra;

import com.recipe.app.src.ingredient.infra.IngredientEntity;
import com.recipe.app.src.user.domain.User;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface FridgeBasketJpaRepository extends CrudRepository<FridgeBasketEntity, Integer> {

    List<FridgeBasketEntity> findByUser(User user);

    Optional<FridgeBasketEntity> findByUserAndFridgeBasketId(User user, Long fridgeBasketId);

    long countByUser(User user);

    Optional<FridgeBasketEntity> findByIngredientAndUser(IngredientEntity ingredient, User user);
}
