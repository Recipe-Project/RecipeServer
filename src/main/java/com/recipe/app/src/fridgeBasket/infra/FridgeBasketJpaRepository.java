package com.recipe.app.src.fridgeBasket.infra;

import com.recipe.app.src.ingredient.infra.IngredientEntity;
import com.recipe.app.src.user.infra.UserEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface FridgeBasketJpaRepository extends CrudRepository<FridgeBasketEntity, Integer> {

    List<FridgeBasketEntity> findByUser(UserEntity user);

    List<FridgeBasketEntity> saveAll(List<FridgeBasketEntity> fridgeBasketEntities);

    Optional<FridgeBasketEntity> findByUserAndFridgeBasketId(UserEntity user, Long fridgeBasketId);

    long countByUser(UserEntity user);

    List<FridgeBasketEntity> findByFridgeBasketIdIn(List<Long> fridgeBasketIds);

    Optional<FridgeBasketEntity> findByIngredientAndUser(IngredientEntity ingredient, UserEntity user);
}
