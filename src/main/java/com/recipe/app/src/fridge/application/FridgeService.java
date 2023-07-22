package com.recipe.app.src.fridge.application;

import com.recipe.app.common.exception.BaseException;
import com.recipe.app.src.fridge.application.dto.FridgeDto;
import com.recipe.app.src.fridge.domain.Fridge;
import com.recipe.app.src.fridge.mapper.FridgeRepository;
import com.recipe.app.src.fridge.models.PatchFcmTokenReq;
import com.recipe.app.src.fridge.models.ShelfLifeUser;
import com.recipe.app.src.fridgeBasket.mapper.FridgeBasketRepository;
import com.recipe.app.src.fridgeBasket.domain.FridgeBasket;
import com.recipe.app.src.ingredient.mapper.IngredientCategoryRepository;
import com.recipe.app.src.ingredient.domain.IngredientCategory;
import com.recipe.app.src.recipe.mapper.RecipeInfoRepository;
import com.recipe.app.src.recipe.domain.RecipeInfo;
import com.recipe.app.src.user.domain.User;
import com.recipe.app.src.user.mapper.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.recipe.app.common.response.BaseResponseStatus.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FridgeService {

    private static final String FIREBASE_API_URL = "https://fcm.googleapis.com/fcm/send";
    private final FridgeRepository fridgeRepository;
    private final FridgeBasketRepository fridgeBasketRepository;
    private final IngredientCategoryRepository ingredientCategoryRepository;
    private final RecipeInfoRepository recipeInfoRepository;
    private final UserRepository userRepository;

    @Transactional
    public List<Fridge> createFridges(FridgeDto.FridgesRequest request, User user) {

        List<IngredientCategory> ingredientCategories = ingredientCategoryRepository.findByStatus("ACTIVE");
        List<Fridge> fridges = request.getFridgeBasketList().stream()
                .map((f) -> new Fridge(user, f.getIngredientName(), f.getIngredientIcon(), ingredientCategories.stream()
                        .filter((i) -> i.getIngredientCategoryIdx().equals(f.getIngredientCategoryIdx()))
                        .findAny().orElse(null), f.getStorageMethod(), f.getExpiredAt(), f.getCount()))
                .collect(Collectors.toList());

        List<String> ingredientNames = fridges.stream().map(Fridge::getIngredientName).collect(Collectors.toList());
        List<FridgeBasket> fridgeBaskets = fridgeBasketRepository.findAllByUserAndStatusAndIngredientNameIn(user, "ACTIVE", ingredientNames);

        if (fridges.size() != fridgeBaskets.size())
            throw new BaseException(FAILED_TO_GET_FRIDGE_BASKET);

        List<Fridge> existIngredients = getExistIngredients(ingredientNames, user);
        if (existIngredients.size() > 0)
            throw new BaseException(POST_FRIDGES_EXIST_INGREDIENT_NAME, existIngredients.get(0).getIngredientName());

        fridgeRepository.saveAll(fridges);
        fridgeBasketRepository.deleteAll(fridgeBaskets);

        return fridgeRepository.findAllByUserAndStatus(user, "ACTIVE");
    }

    public List<Fridge> retrieveFridges(User user) {
        return fridgeRepository.findAllByUserAndStatus(user, "ACTIVE");
    }

    @Transactional
    public void deleteFridgeIngredients(User user, FridgeDto.FridgeIngredientsRequest request) {
        List<String> ingredientNames = request.getIngredientName();
        List<Fridge> existIngredients = getExistIngredients(ingredientNames, user);
        if (existIngredients.size() != ingredientNames.size())
            throw new BaseException(NOT_FOUND_INGREDIENT);

        fridgeRepository.deleteAll(existIngredients);
    }

    @Transactional
    public void updateFridgeIngredients(User user, FridgeDto.PatchFridgesRequest request) {

        List<String> ingredientNames = request.getPatchFridgeList().stream().map(FridgeDto.PatchFridgeRequest::getIngredientName).collect(Collectors.toList());
        List<Fridge> fridges = getExistIngredients(ingredientNames, user);
        if (fridges.size() != ingredientNames.size())
            throw new BaseException(NOT_FOUND_INGREDIENT);

        for (Fridge fridge : fridges) {
            Optional<FridgeDto.PatchFridgeRequest> patchRequest = request.getPatchFridgeList().stream()
                    .filter((pf) -> pf.getIngredientName().equals(fridge.getIngredientName()))
                    .findFirst();

            patchRequest.ifPresent(patchFridgeRequest -> {
                fridge.setIngredientName(patchFridgeRequest.getIngredientName());
                fridge.setExpiredAt(patchFridgeRequest.getExpiredAt());
                fridge.setStorageMethod(patchFridgeRequest.getStorageMethod());
                fridge.setCount(patchFridgeRequest.getCount());
            });
        }

        fridgeRepository.saveAll(fridges);
    }

    @Transactional
    public void updateFcmToken(PatchFcmTokenReq patchFcmTokenReq, User user) {
        String fcmToken = patchFcmTokenReq.getFcmToken();
        user.setDeviceToken(fcmToken);
        userRepository.save(user);
    }

    public List<Fridge> getExistIngredients(List<String> ingredientNameList, User user) throws BaseException {
        return fridgeRepository.findAllByUserAndStatusAndIngredientNameIn(user, "ACTIVE", ingredientNameList);
    }

    public List<RecipeInfo> retrieveFridgeRecipes(User user, Integer start, Integer display) {
        return recipeInfoRepository.searchRecipeListOrderByIngredientCntWhichUserHasDesc(user, "ACTIVE").stream()
                .skip(start == 0 ? 0 : start - 1)
                .limit(display)
                .collect(Collectors.toList());
    }

    public int countFridgeRecipes(User user) {
        return recipeInfoRepository.searchRecipeListOrderByIngredientCntWhichUserHasDesc(user, "ACTIVE").size();
    }

    public List<ShelfLifeUser> retreiveShelfLifeUserList() throws BaseException {
        SimpleDateFormat sdFormat = new SimpleDateFormat("yy.MM.dd");
        String today = sdFormat.format(new Date());

        List<Fridge> fridgeList = fridgeRepository.findAllByStatusAnd3DaysBeforeExpiredAt("ACTIVE", today);

        List<ShelfLifeUser> shelfLifeUsers = new ArrayList<>();
        for (Fridge fridge : fridgeList) {
            String deviceToken = fridge.getUser().getDeviceToken();
            String ingredientName = fridge.getIngredientName();
            ShelfLifeUser shelfLifeUser = new ShelfLifeUser(deviceToken, ingredientName);
            shelfLifeUsers.add(shelfLifeUser);
        }
        return shelfLifeUsers;
    }
}
