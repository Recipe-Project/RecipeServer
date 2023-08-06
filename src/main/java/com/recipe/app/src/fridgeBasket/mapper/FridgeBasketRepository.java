package com.recipe.app.src.fridgeBasket.mapper;

import com.recipe.app.src.fridgeBasket.domain.FridgeBasket;
import com.recipe.app.src.ingredient.infra.IngredientEntity;
import com.recipe.app.src.user.infra.UserEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FridgeBasketRepository extends CrudRepository<FridgeBasket, Integer> {
    Optional<FridgeBasket> findByUserAndIngredientNameAndStatus(UserEntity user, String name, String status);

    Long countByUserAndStatus(UserEntity user, String status);

    List<FridgeBasket> findByUserAndStatus(UserEntity user, String status);

    List<FridgeBasket> findAllByUserAndStatusAndIngredientIn(UserEntity user, String status, List<IngredientEntity> ingredientEntityList);

    List<FridgeBasket> findAllByUserAndStatusAndIngredientNameIn(UserEntity user, String status, List<String> ingredientNameList);
}
