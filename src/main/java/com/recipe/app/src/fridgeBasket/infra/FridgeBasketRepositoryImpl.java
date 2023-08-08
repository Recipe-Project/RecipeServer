package com.recipe.app.src.fridgeBasket.infra;

import com.recipe.app.src.fridgeBasket.application.port.FridgeBasketRepository;
import com.recipe.app.src.fridgeBasket.domain.FridgeBasket;
import com.recipe.app.src.ingredient.domain.Ingredient;
import com.recipe.app.src.ingredient.infra.IngredientEntity;
import com.recipe.app.src.user.domain.User;
import com.recipe.app.src.user.infra.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class FridgeBasketRepositoryImpl implements FridgeBasketRepository {

    private final FridgeBasketJpaRepository fridgeBasketJpaRepository;

    @Override
    public List<FridgeBasket> saveAll(List<FridgeBasket> fridgeBaskets) {
        return fridgeBasketJpaRepository.saveAll(fridgeBaskets.stream()
                        .map(FridgeBasketEntity::fromModel)
                        .collect(Collectors.toList())).stream()
                .map(FridgeBasketEntity::toModel)
                .collect(Collectors.toList());
    }

    @Override
    public List<FridgeBasket> findByUser(User user) {
        return fridgeBasketJpaRepository.findByUser(UserEntity.fromModel(user)).stream()
                .map(FridgeBasketEntity::toModel)
                .collect(Collectors.toList());
    }

    @Override
    public List<FridgeBasket> findByUserAndFridgeBasketIdIn(User user, List<Long> fridgeBasketIds) {
        return fridgeBasketJpaRepository.findByUserAndFridgeBasketIdIn(UserEntity.fromModel(user), fridgeBasketIds).stream()
                .map(FridgeBasketEntity::toModel)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteAll(List<FridgeBasket> fridgeBaskets) {
        fridgeBasketJpaRepository.deleteAll(fridgeBaskets.stream().map(FridgeBasketEntity::fromModel).collect(Collectors.toList()));
    }

    @Override
    public long countByUser(User user) {
        return fridgeBasketJpaRepository.countByUser(UserEntity.fromModel(user));
    }

    @Override
    public FridgeBasket save(FridgeBasket fridgeBasket) {
        return fridgeBasketJpaRepository.save(FridgeBasketEntity.fromModel(fridgeBasket)).toModel();
    }

    @Override
    public List<FridgeBasket> findByFridgeBasketIdIn(List<Long> fridgeBasketIds) {
        return fridgeBasketJpaRepository.findByFridgeBasketIdIn(fridgeBasketIds).stream().map(FridgeBasketEntity::toModel).collect(Collectors.toList());
    }

    @Override
    public Optional<FridgeBasket> findByIngredientAndUser(Ingredient ingredient, User user) {
        return fridgeBasketJpaRepository.findByIngredientAndUser(IngredientEntity.fromModel(ingredient), UserEntity.fromModel(user)).map(FridgeBasketEntity::toModel);
    }
}
