package com.recipe.app.src.fridge.application;

import com.recipe.app.src.fridge.application.dto.FridgeDto;
import com.recipe.app.src.fridge.application.port.FridgeRepository;
import com.recipe.app.src.fridge.domain.Fridge;
import com.recipe.app.src.fridge.exception.FridgeSaveExpiredDateNotMatchException;
import com.recipe.app.src.fridge.exception.FridgeSaveUnitNotMatchException;
import com.recipe.app.src.fridge.exception.NotFoundFridgeException;
import com.recipe.app.src.fridgeBasket.application.FridgeBasketService;
import com.recipe.app.src.fridgeBasket.domain.FridgeBasket;
import com.recipe.app.src.ingredient.domain.Ingredient;
import com.recipe.app.src.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FridgeService {

    private static final String FIREBASE_API_URL = "https://fcm.googleapis.com/fcm/send";
    private final FridgeRepository fridgeRepository;
    private final FridgeBasketService fridgeBasketService;

    @Transactional
    public List<Fridge> createFridges(User user) {

        List<FridgeBasket> fridgeBaskets = fridgeBasketService.getFridgeBasketsByUser(user);
        List<Fridge> existFridges = getFridges(user);
        Map<Ingredient, Fridge> existIngredients = existFridges.stream().collect(Collectors.toMap(Fridge::getIngredient, v -> v));

        List<Fridge> fridges = fridgeBaskets.stream()
                .map(fridgeBasket -> {
                    Ingredient ingredient = fridgeBasket.getIngredient();
                    if (existIngredients.containsKey(ingredient)) {
                        Fridge fridge = existIngredients.get(ingredient);
                        if ((fridge.getExpiredAt() != null && !fridge.getExpiredAt().equals(fridgeBasket.getExpiredAt()))
                        || (fridgeBasket.getExpiredAt() != null && !fridgeBasket.getExpiredAt().equals(fridge.getExpiredAt())))
                            throw new FridgeSaveExpiredDateNotMatchException(ingredient.getIngredientName());
                        if (!fridge.getUnit().equals(fridgeBasket.getUnit()))
                            throw new FridgeSaveUnitNotMatchException(ingredient.getIngredientName());

                        fridge = fridge.plusQuantity(fridgeBasket.getQuantity());
                        return fridge;
                    }
                    return Fridge.from(fridgeBasket);
                })
                .collect(Collectors.toList());

        fridgeRepository.saveAll(fridges);
        fridgeBasketService.deleteFridgeBaskets(fridgeBaskets);

        return getFridges(user);
    }

    public List<Fridge> getFridges(User user) {
        return fridgeRepository.findByUser(user);
    }

    @Transactional
    public List<Fridge> deleteFridge(User user, Long fridgeId) {

        Fridge fridge = getFridge(user, fridgeId);
        fridgeRepository.delete(fridge);

        return getFridges(user);
    }

    @Transactional
    public List<Fridge> updateFridge(User user, Long fridgeId, FridgeDto.FridgeRequest request) {

        Fridge fridge = getFridge(user, fridgeId);
        fridge = fridge.changeExpiredAtAndQuantityAndUnit(request.getExpiredAt(), request.getQuantity(), request.getUnit());
        fridgeRepository.save(fridge);

        return getFridges(user);
    }

    public Fridge getFridge(User user, Long fridgeId) {
        return fridgeRepository.findByUserAndFridgeId(user, fridgeId).orElseThrow(NotFoundFridgeException::new);
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
