package com.recipe.app.src.fridgeBasket.application;

import com.google.common.base.Preconditions;
import com.recipe.app.src.common.application.BadWordService;
import com.recipe.app.src.fridgeBasket.application.dto.FridgeBasketCountResponse;
import com.recipe.app.src.fridgeBasket.application.dto.FridgeBasketIngredientIdsRequest;
import com.recipe.app.src.fridgeBasket.application.dto.FridgeBasketIngredientRequest;
import com.recipe.app.src.fridgeBasket.application.dto.FridgeBasketRequest;
import com.recipe.app.src.fridgeBasket.application.dto.FridgeBasketsResponse;
import com.recipe.app.src.fridgeBasket.domain.FridgeBasket;
import com.recipe.app.src.fridgeBasket.exception.NotFoundFridgeBasketException;
import com.recipe.app.src.fridgeBasket.infra.FridgeBasketRepository;
import com.recipe.app.src.ingredient.application.IngredientCategoryService;
import com.recipe.app.src.ingredient.application.IngredientService;
import com.recipe.app.src.ingredient.domain.Ingredient;
import com.recipe.app.src.ingredient.domain.IngredientCategory;
import com.recipe.app.src.user.domain.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class FridgeBasketService {

    private final FridgeBasketRepository fridgeBasketRepository;
    private final IngredientService ingredientService;
    private final IngredientCategoryService ingredientCategoryService;
    private final BadWordService badWordService;

    public FridgeBasketService(FridgeBasketRepository fridgeBasketRepository, IngredientService ingredientService,
                               IngredientCategoryService ingredientCategoryService, BadWordService badWordService) {
        this.fridgeBasketRepository = fridgeBasketRepository;
        this.ingredientService = ingredientService;
        this.ingredientCategoryService = ingredientCategoryService;
        this.badWordService = badWordService;
    }

    @Transactional
    public void createFridgeBasketsByIngredientId(User user, FridgeBasketIngredientIdsRequest request) {

        Objects.requireNonNull(request.getIngredientIds(), "재료 아이디 목록을 입력해주세요.");

        List<Ingredient> ingredients = ingredientService.findByIngredientIds(request.getIngredientIds());
        List<FridgeBasket> existFridgeBaskets = findByUserId(user.getUserId());
        Map<Long, FridgeBasket> fridgeBasketMapByIngredientId = existFridgeBaskets.stream()
                .collect(Collectors.toMap(FridgeBasket::getIngredientId, Function.identity()));

        List<FridgeBasket> fridgeBaskets = ingredients.stream()
                .map(ingredient -> {
                    FridgeBasket fridgeBasket = fridgeBasketMapByIngredientId.getOrDefault(
                            ingredient.getIngredientId(),
                            FridgeBasket.builder()
                                    .userId(user.getUserId())
                                    .ingredientId(ingredient.getIngredientId())
                                    .build());
                    fridgeBasket.plusQuantity(1);
                    return fridgeBasket;
                })
                .collect(Collectors.toList());

        fridgeBasketRepository.saveAll(fridgeBaskets);
    }

    @Transactional
    public void createFridgeBasketWithIngredientSave(User user, FridgeBasketIngredientRequest request) {

        Preconditions.checkArgument(StringUtils.hasText(request.getIngredientName()), "재료명을 입력해주세요.");
        Objects.requireNonNull(request.getIngredientCategoryId(), "재료 카테고리 아이디를 입력해주세요.");
        badWordService.checkBadWords(request.getIngredientName());

        IngredientCategory ingredientCategory = ingredientCategoryService.findById(request.getIngredientCategoryId());
        Ingredient ingredient = ingredientService.findByUserIdAndIngredientNameAndIngredientIconUrlAndIngredientCategoryId(
                        user.getUserId(),
                        request.getIngredientName(),
                        request.getIngredientIconUrl(),
                        ingredientCategory.getIngredientCategoryId())
                .orElseGet(() -> Ingredient.builder()
                        .userId(user.getUserId())
                        .ingredientName(request.getIngredientName())
                        .ingredientIconUrl(request.getIngredientIconUrl())
                        .ingredientCategoryId(ingredientCategory.getIngredientCategoryId())
                        .build());
        ingredientService.createIngredient(ingredient);

        FridgeBasket fridgeBasket = fridgeBasketRepository.findByIngredientIdAndUserId(ingredient.getIngredientId(), user.getUserId())
                .orElseGet(() -> FridgeBasket.builder()
                        .userId(user.getUserId())
                        .ingredientId(ingredient.getIngredientId())
                        .build());
        fridgeBasket.plusQuantity(1);
        fridgeBasketRepository.save(fridgeBasket);
    }

    @Transactional
    public void deleteFridgeBasket(User user, Long fridgeBasketId) {

        FridgeBasket fridgeBasket = findByUserIdAndFridgeBasketId(user.getUserId(), fridgeBasketId);
        fridgeBasketRepository.delete(fridgeBasket);
    }

    @Transactional
    public void updateFridgeBasket(User user, Long fridgeBasketId, FridgeBasketRequest request) {

        FridgeBasket fridgeBasket = findByUserIdAndFridgeBasketId(user.getUserId(), fridgeBasketId);
        fridgeBasket.updateFridgeBasket(request.getExpiredAt(), request.getQuantity(), request.getUnit());
        fridgeBasketRepository.save(fridgeBasket);
    }

    @Transactional(readOnly = true)
    public FridgeBasketCountResponse countFridgeBasketByUser(User user) {

        return FridgeBasketCountResponse.from(countByUserId(user.getUserId()));
    }

    @Transactional(readOnly = true)
    public long countByUserId(Long userId) {

        return fridgeBasketRepository.countByUserId(userId);
    }

    @Transactional(readOnly = true)
    public FridgeBasketsResponse getFridgeBasketsByUser(User user) {

        List<FridgeBasket> fridgeBaskets = findByUserId(user.getUserId());
        List<IngredientCategory> ingredientCategories = ingredientCategoryService.findAll();
        List<Long> ingredientIds = fridgeBaskets.stream()
                .map(FridgeBasket::getIngredientId)
                .collect(Collectors.toList());
        List<Ingredient> ingredients = ingredientService.findByIngredientIds(ingredientIds);

        return FridgeBasketsResponse.from(fridgeBaskets, ingredientCategories, ingredients);
    }

    @Transactional
    public void deleteFridgeBasketsByUser(User user) {

        List<FridgeBasket> fridgeBaskets = findByUserId(user.getUserId());
        deleteFridgeBaskets(fridgeBaskets);
    }

    @Transactional
    public void deleteFridgeBaskets(List<FridgeBasket> fridgeBaskets) {

        fridgeBasketRepository.deleteAll(fridgeBaskets);
    }

    private FridgeBasket findByUserIdAndFridgeBasketId(Long userId, Long fridgeBasketId) {

        return fridgeBasketRepository.findByUserIdAndFridgeBasketId(userId, fridgeBasketId).orElseThrow(NotFoundFridgeBasketException::new);
    }

    @Transactional(readOnly = true)
    public List<FridgeBasket> findByUserId(Long userId) {

        return fridgeBasketRepository.findByUserId(userId);
    }
}
