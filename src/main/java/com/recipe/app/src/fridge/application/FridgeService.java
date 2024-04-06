package com.recipe.app.src.fridge.application;

import com.recipe.app.src.fridge.application.dto.FridgeRequest;
import com.recipe.app.src.fridge.application.dto.FridgeResponse;
import com.recipe.app.src.fridge.application.dto.FridgesResponse;
import com.recipe.app.src.fridge.domain.Fridge;
import com.recipe.app.src.fridge.exception.FridgeSaveExpiredDateNotMatchException;
import com.recipe.app.src.fridge.exception.FridgeSaveUnitNotMatchException;
import com.recipe.app.src.fridge.exception.NotFoundFridgeException;
import com.recipe.app.src.fridge.infra.FridgeRepository;
import com.recipe.app.src.fridgeBasket.application.FridgeBasketService;
import com.recipe.app.src.fridgeBasket.domain.FridgeBasket;
import com.recipe.app.src.ingredient.application.IngredientCategoryService;
import com.recipe.app.src.ingredient.application.IngredientService;
import com.recipe.app.src.ingredient.domain.Ingredient;
import com.recipe.app.src.ingredient.domain.IngredientCategory;
import com.recipe.app.src.user.domain.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class FridgeService {

    private final FridgeRepository fridgeRepository;
    private final FridgeBasketService fridgeBasketService;
    private final IngredientService ingredientService;
    private final IngredientCategoryService ingredientCategoryService;

    public FridgeService(FridgeRepository fridgeRepository, FridgeBasketService fridgeBasketService, IngredientService ingredientService, IngredientCategoryService ingredientCategoryService) {
        this.fridgeRepository = fridgeRepository;
        this.fridgeBasketService = fridgeBasketService;
        this.ingredientService = ingredientService;
        this.ingredientCategoryService = ingredientCategoryService;
    }

    @Transactional
    public void createFridges(User user) {

        List<FridgeBasket> fridgeBaskets = fridgeBasketService.findByUserId(user.getUserId());
        List<Fridge> existFridges = findByUserId(user.getUserId());
        Map<Long, Fridge> fridgeMapByIngredientId = existFridges.stream()
                .collect(Collectors.toMap(Fridge::getIngredientId, Function.identity()));
        List<Long> ingredientIds = existFridges.stream()
                .map(Fridge::getIngredientId)
                .collect(Collectors.toList());
        Map<Long, Ingredient> ingredientMapById = ingredientService.findByIngredientIds(ingredientIds).stream()
                .collect(Collectors.toMap(Ingredient::getIngredientId, Function.identity()));

        List<Fridge> fridges = fridgeBaskets.stream()
                .map(fridgeBasket -> {
                    Fridge fridge = fridgeMapByIngredientId.getOrDefault(fridgeBasket.getIngredientId(), fridgeBasket.toFridge());
                    fridge.plusQuantity(fridgeBasket.getQuantity());
                    Ingredient ingredient = ingredientMapById.get(fridge.getIngredientId());
                    checkFridgeExpiredDateMatch(fridgeBasket, fridge, ingredient);
                    checkFridgeUnitMatch(fridgeBasket, fridge, ingredient);
                    return fridge;
                })
                .collect(Collectors.toList());

        fridgeRepository.saveAll(fridges);
        fridgeBasketService.deleteFridgeBaskets(fridgeBaskets);
    }

    private void checkFridgeUnitMatch(FridgeBasket fridgeBasket, Fridge fridge, Ingredient ingredient) {
        if ((fridge.getUnit() == null && fridgeBasket.getUnit() != null)
                || (fridge.getUnit() != null && !fridge.getUnit().equals(fridgeBasket.getUnit())))
            throw new FridgeSaveUnitNotMatchException(ingredient.getIngredientName());
    }

    private void checkFridgeExpiredDateMatch(FridgeBasket fridgeBasket, Fridge fridge, Ingredient ingredient) {
        if ((fridge.getExpiredAt() != null && !fridge.getExpiredAt().equals(fridgeBasket.getExpiredAt()))
                || (fridgeBasket.getExpiredAt() != null && !fridgeBasket.getExpiredAt().equals(fridge.getExpiredAt())))
            throw new FridgeSaveExpiredDateNotMatchException(ingredient.getIngredientName());
    }

    @Transactional(readOnly = true)
    public List<Fridge> findByUserId(Long userId) {
        return fridgeRepository.findByUserId(userId);
    }

    @Transactional
    public void deleteFridge(User user, Long fridgeId) {

        Fridge fridge = findByUserIdAndFridgeId(user.getUserId(), fridgeId);
        fridgeRepository.delete(fridge);
    }

    @Transactional
    public void updateFridge(User user, Long fridgeId, FridgeRequest request) {

        Fridge fridge = findByUserIdAndFridgeId(user.getUserId(), fridgeId);
        fridge.updateFridge(request.getExpiredAt(), request.getQuantity(), request.getUnit());
        fridgeRepository.save(fridge);
    }

    @Transactional(readOnly = true)
    public FridgesResponse getFridges(User user) {

        long fridgeBasketCount = fridgeBasketService.countByUserId(user.getUserId());
        List<Fridge> fridges = findByUserId(user.getUserId());
        List<IngredientCategory> ingredientCategories = ingredientCategoryService.findAll();
        List<Long> ingredientIds = fridges.stream()
                .map(Fridge::getIngredientId)
                .collect(Collectors.toList());
        List<Ingredient> ingredients = ingredientService.findByIngredientIds(ingredientIds);

        return FridgesResponse.from(fridgeBasketCount, fridges, ingredientCategories, ingredients);
    }

    @Transactional(readOnly = true)
    public FridgeResponse getFridge(User user, Long fridgeId) {

        Fridge fridge = findByUserIdAndFridgeId(user.getUserId(), fridgeId);
        Ingredient ingredient = ingredientService.findByIngredientId(fridge.getIngredientId());

        return FridgeResponse.from(fridge, ingredient);
    }

    @Transactional
    public void deleteFridgesByUser(User user) {

        List<Fridge> fridges = fridgeRepository.findByUserId(user.getUserId());
        fridgeRepository.deleteAll(fridges);
    }

    private Fridge findByUserIdAndFridgeId(Long userId, Long fridgeId) {

        return fridgeRepository.findByUserIdAndFridgeId(userId, fridgeId).orElseThrow(NotFoundFridgeException::new);
    }

    @Transactional(readOnly = true)
    public boolean isInFridge(Long userId, Ingredient ingredient) {

        List<Fridge> fridges = fridgeRepository.findByUserId(userId);
        List<Long> ingredientIds = fridges.stream()
                .map(Fridge::getIngredientId)
                .collect(Collectors.toList());
        List<Ingredient> ingredients = ingredientService.findByIngredientIds(ingredientIds);
        List<String> ingredientNames = ingredients.stream()
                .map(Ingredient::getIngredientName)
                .collect(Collectors.toList());

        return ingredientNames.contains(ingredient.getIngredientName())
                || ingredient.existInIngredients(ingredients);
    }

    @Transactional(readOnly = true)
    public List<Ingredient> findIngredientsInUserFridge(User user) {

        List<Long> ingredientIds = findByUserId(user.getUserId()).stream()
                .map(Fridge::getIngredientId)
                .collect(Collectors.toList());

        return ingredientService.findByIngredientIds(ingredientIds);
    }

     /*

    public List<ShelfLifeUser> retreiveShelfLifeUserList() throws BaseException {
        SimpleDateFormat sdFormat = new SimpleDateFormat("yy.MM.dd");
        String today = sdFormat.format(new Date());

        List<Fridge> fridges = fridgeRepository.findAllByStatusAnd3DaysBeforeExpiredAt("ACTIVE", today);

        List<ShelfLifeUser> shelfLifeUsers = new ArrayList<>();
        for (FridgeEntity fridgeEntity : fridgeEntityList) {
            String deviceToken = fridgeEntity.getUser().getDeviceToken();
            String ingredientName = fridgeEntity.getIngredient().getIngredientName();
            ShelfLifeUser shelfLifeUser = new ShelfLifeUser(deviceToken, ingredientName);
            shelfLifeUsers.add(shelfLifeUser);
        }
        return shelfLifeUsers;
    }

     */
}
