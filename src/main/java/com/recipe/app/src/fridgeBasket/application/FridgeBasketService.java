package com.recipe.app.src.fridgeBasket.application;

import com.recipe.app.common.exception.BaseException;
import com.recipe.app.src.fridgeBasket.application.dto.FridgeBasketDto;
import com.recipe.app.src.fridgeBasket.domain.FridgeBasket;
import com.recipe.app.src.fridgeBasket.mapper.FridgeBasketRepository;
import com.recipe.app.src.ingredient.application.port.IngredientRepository;
import com.recipe.app.src.ingredient.infra.IngredientCategoryEntity;
import com.recipe.app.src.ingredient.infra.IngredientCategoryJpaRepository;
import com.recipe.app.src.ingredient.infra.IngredientEntity;
import com.recipe.app.src.ingredient.infra.IngredientJpaRepository;
import com.recipe.app.src.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.recipe.app.common.response.BaseResponseStatus.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FridgeBasketService {

    private final FridgeBasketRepository fridgeBasketRepository;
    private final IngredientCategoryJpaRepository ingredientCategoryRepository;
    private final IngredientJpaRepository ingredientRepository;

    @Transactional
    public void createFridgesBasket(FridgeBasketDto.FridgeBasketIdsRequest request, User user) {
        List<IngredientEntity> ingredients = ingredientRepository.findByIngredientIdIn(request.getIngredientList());
        Map<String, FridgeBasket> existFridgeBaskets = fridgeBasketRepository.findAllByUserAndStatusAndIngredientIn(user, "ACTIVE", ingredients)
                .stream().collect(Collectors.toMap(FridgeBasket::getIngredientName, v -> v));

        List<FridgeBasket> fridgeBaskets = ingredients.stream()
                .map(ingredient -> {
                    if (existFridgeBaskets.containsKey(ingredient.getIngredientName())) {
                        FridgeBasket fridgeBasket = existFridgeBaskets.get(ingredient.getIngredientName());
                        fridgeBasket.setCount(fridgeBasket.getCount() + 1);
                        return fridgeBasket;
                    }
                    return new FridgeBasket(user, ingredient, ingredient.getIngredientName(), ingredient.getIngredientIconUrl(), ingredient.getIngredientCategoryEntity());
                })
                .collect(Collectors.toList());

        fridgeBasketRepository.saveAll(fridgeBaskets);
    }

    @Transactional
    public FridgeBasket createDirectFridgeBasket(FridgeBasketDto.DirectFridgeBasketsRequest request, User user) {

        fridgeBasketRepository.findByUserAndIngredientNameAndStatus(user, request.getIngredientName(), "ACTIVE")
                .ifPresent(fridgeBasket -> {
                    throw new BaseException(POST_FRIDGES_BASKET_EXIST_INGREDIENT_NAME, fridgeBasket.getIngredientName());
                });

        ingredientRepository.findByIngredientName(request.getIngredientName())
                .ifPresent((ingredient -> {
                    throw new BaseException(POST_FRIDGES_DIRECT_BASKET_DUPLICATED_INGREDIENT_NAME_IN_INGREDIENTS, ingredient.getIngredientName());
                }));

        IngredientCategoryEntity ingredientCategoryEntity = ingredientCategoryRepository.findById(request.getIngredientCategoryIdx()).orElseThrow(() -> {
            throw new BaseException(NOT_FOUND_INGREDIENT_CATEGORY);
        });

        FridgeBasket fridgeBasket = new FridgeBasket(user, null, request.getIngredientName(), request.getIngredientIcon(), ingredientCategoryEntity);
        fridgeBasket = fridgeBasketRepository.save(fridgeBasket);

        return fridgeBasket;
    }

    @Transactional
    public void deleteFridgeBasket(User user, String ingredient) {
        FridgeBasket fridgeBasket = fridgeBasketRepository.findByUserAndIngredientNameAndStatus(user, ingredient, "ACTIVE")
                .orElseThrow(() -> {
                    throw new BaseException(FAILED_TO_RETREIVE_FRIDGE_BASKET_BY_NAME);
                });

        fridgeBasketRepository.delete(fridgeBasket);
    }

    @Transactional
    public void updateFridgeBaskets(FridgeBasketDto.FridgeBasketsRequest request, User user) {
        List<String> ingredientNames = request.getFridgeBasketList().stream().map(FridgeBasketDto.FridgeBasketRequest::getIngredientName).collect(Collectors.toList());
        Map<String, FridgeBasket> existFridgeBaskets = fridgeBasketRepository.findAllByUserAndStatusAndIngredientNameIn(user, "ACTIVE", ingredientNames).stream()
                .collect(Collectors.toMap(FridgeBasket::getIngredientName, v -> v));

        List<FridgeBasket> fridgeBaskets = request.getFridgeBasketList().stream()
                .map((f) -> {
                    if (!existFridgeBaskets.containsKey(f.getIngredientName())) {
                        throw new BaseException(FAILED_TO_RETREIVE_FRIDGE_BASKET_BY_NAME);
                    }
                    FridgeBasket fridgeBasket = existFridgeBaskets.get(f.getIngredientName());
                    fridgeBasket.setCount(f.getIngredientCnt());
                    fridgeBasket.setStorageMethod(f.getStorageMethod());
                    fridgeBasket.setExpiredAt(f.getExpiredAt());
                    return fridgeBasket;
                })
                .collect(Collectors.toList());
        fridgeBasketRepository.saveAll(fridgeBaskets);
    }

    public long countFridgeBasketsByUser(User user) {
        return fridgeBasketRepository.countByUserAndStatus(user, "ACTIVE");
    }

    public List<FridgeBasket> retrieveFridgeBasketsByUser(User user) {
        return fridgeBasketRepository.findByUserAndStatus(user, "ACTIVE");
    }
}
