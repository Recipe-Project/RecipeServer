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
import java.util.stream.StreamSupport;

@Repository
@RequiredArgsConstructor
public class FridgeBasketRepositoryImpl implements FridgeBasketRepository {

    private final FridgeBasketJpaRepository fridgeBasketJpaRepository;

    @Override
    public List<FridgeBasket> saveAll(List<FridgeBasket> fridgeBaskets) {
        return StreamSupport.stream(fridgeBasketJpaRepository.saveAll(fridgeBaskets.stream()
                        .map(FridgeBasketEntity::fromModel)
                        .collect(Collectors.toList())).spliterator(), false)
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
    public Optional<FridgeBasket> findByUserAndFridgeBasketId(User user, Long fridgeBasketId) {
        return fridgeBasketJpaRepository.findByUserAndFridgeBasketId(UserEntity.fromModel(user), fridgeBasketId).map(FridgeBasketEntity::toModel);
    }

    @Override
    public void delete(FridgeBasket fridgeBasket) {
        fridgeBasketJpaRepository.delete(FridgeBasketEntity.fromModel(fridgeBasket));
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
    public Optional<FridgeBasket> findByIngredientAndUser(Ingredient ingredient, User user) {
        return fridgeBasketJpaRepository.findByIngredientAndUser(IngredientEntity.fromModel(ingredient), UserEntity.fromModel(user)).map(FridgeBasketEntity::toModel);
    }
}
