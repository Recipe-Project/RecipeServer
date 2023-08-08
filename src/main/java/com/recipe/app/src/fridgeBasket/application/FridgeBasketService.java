package com.recipe.app.src.fridgeBasket.application;

import com.recipe.app.src.fridgeBasket.application.dto.FridgeBasketDto;
import com.recipe.app.src.fridgeBasket.application.port.FridgeBasketRepository;
import com.recipe.app.src.fridgeBasket.domain.FridgeBasket;
import com.recipe.app.src.fridgeBasket.exception.NotFoundFridgeBasketException;
import com.recipe.app.src.ingredient.application.IngredientService;
import com.recipe.app.src.ingredient.domain.Ingredient;
import com.recipe.app.src.ingredient.domain.IngredientCategory;
import com.recipe.app.src.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FridgeBasketService {

    private final FridgeBasketRepository fridgeBasketRepository;
    private final IngredientService ingredientService;

    @Transactional
    public List<FridgeBasket> createFridgeBasketByIngredientId(User user, FridgeBasketDto.FridgeBasketIngredientIdsRequest request) {
        List<Ingredient> ingredients = ingredientService.getIngredientsByIngredientIds(request.getIngredientIds());
        List<FridgeBasket> existFridgeBaskets = fridgeBasketRepository.findByUser(user);
        Map<Ingredient, FridgeBasket> existIngredients = existFridgeBaskets.stream().collect(Collectors.toMap(FridgeBasket::getIngredient, v -> v));

        List<FridgeBasket> fridgeBaskets = ingredients.stream()
                .map(ingredient -> {
                    if (existIngredients.containsKey(ingredient)) {
                        FridgeBasket fridgeBasket = existIngredients.get(ingredient);
                        fridgeBasket = fridgeBasket.plusCount(1);
                        return fridgeBasket;
                    }
                    return FridgeBasket.from(user, ingredient);
                })
                .collect(Collectors.toList());

        fridgeBasketRepository.saveAll(fridgeBaskets);

        return getFridgeBasketsByUser(user);
    }

    @Transactional
    public List<FridgeBasket> createFridgeBasketWithIngredientSave(User user, FridgeBasketDto.FridgeBasketIngredientRequest request) {

        IngredientCategory ingredientCategory = ingredientService.getIngredientCategoryByIngredientCategoryId(request.getIngredientCategoryId());
        Ingredient ingredient = ingredientService.getIngredientByUserAndIngredientNameAndIngredientIconUrlAndIngredientCategory(
                        user,
                        request.getIngredientName(),
                        request.getIngredientIconUrl(),
                        ingredientCategory)
                .orElseGet(() -> ingredientService.createIngredient(Ingredient.from(ingredientCategory, request.getIngredientName(), request.getIngredientIconUrl(), user)));

        FridgeBasket fridgeBasket = fridgeBasketRepository.findByIngredientAndUser(ingredient, user)
                .map(f -> f.plusCount(1))
                .orElseGet(() -> FridgeBasket.from(user, ingredient));

        fridgeBasketRepository.save(fridgeBasket);

        return getFridgeBasketsByUser(user);
    }

    @Transactional
    public void deleteFridgeBaskets(User user, FridgeBasketDto.FridgeBasketIdsRequest request) {
        List<FridgeBasket> fridgeBaskets = fridgeBasketRepository.findByUserAndFridgeBasketIdIn(user, request.getFridgeBasketIds());
        fridgeBasketRepository.deleteAll(fridgeBaskets);
    }

    @Transactional
    public List<FridgeBasket> updateFridgeBaskets(User user, FridgeBasketDto.FridgeBasketsRequest request) {
        List<Long> fridgeBasketIds = request.getFridgeBaskets().stream().map(FridgeBasketDto.FridgeBasketRequest::getFridgeBasketId).collect(Collectors.toList());
        List<FridgeBasket> fridgeBaskets = fridgeBasketRepository.findByFridgeBasketIdIn(fridgeBasketIds);
        if (fridgeBasketIds.size() != fridgeBaskets.size())
            throw new NotFoundFridgeBasketException();

        Map<Long, FridgeBasket> fridgeBasketMap = fridgeBaskets.stream().collect(Collectors.toMap(FridgeBasket::getFridgeBasketId, v -> v));
        List<FridgeBasket> updateFridgeBaskets = new ArrayList<>();
        for (FridgeBasketDto.FridgeBasketRequest f : request.getFridgeBaskets()) {
            FridgeBasket fridgeBasket = fridgeBasketMap.get(f.getFridgeBasketId());
            updateFridgeBaskets.add(fridgeBasket.changeExpiredAtAndQuantityAndUnit(f.getExpiredAt(), f.getQuantity(), f.getUnit()));
        }
        fridgeBasketRepository.saveAll(updateFridgeBaskets);

        return getFridgeBasketsByUser(user);
    }

    public long countFridgeBasketByUser(User user) {
        return fridgeBasketRepository.countByUser(user);
    }

    public List<FridgeBasket> getFridgeBasketsByUser(User user) {
        return fridgeBasketRepository.findByUser(user);
    }
}
