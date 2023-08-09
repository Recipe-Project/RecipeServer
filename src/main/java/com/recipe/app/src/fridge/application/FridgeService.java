package com.recipe.app.src.fridge.application;

import com.recipe.app.common.exception.BaseException;
import com.recipe.app.src.fridge.application.dto.FridgeDto;
import com.recipe.app.src.fridge.application.port.FridgeRepository;
import com.recipe.app.src.fridge.domain.Fridge;
import com.recipe.app.src.fridge.exception.NotFoundFridgeException;
import com.recipe.app.src.fridge.infra.FridgeEntity;
import com.recipe.app.src.fridge.models.PatchFcmTokenReq;
import com.recipe.app.src.fridge.models.ShelfLifeUser;
import com.recipe.app.src.fridgeBasket.application.FridgeBasketService;
import com.recipe.app.src.fridgeBasket.domain.FridgeBasket;
import com.recipe.app.src.fridgeBasket.infra.FridgeBasketJpaRepository;
import com.recipe.app.src.recipe.domain.RecipeInfo;
import com.recipe.app.src.recipe.mapper.RecipeInfoRepository;
import com.recipe.app.src.user.application.port.UserRepository;
import com.recipe.app.src.user.domain.User;
import com.recipe.app.src.user.infra.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.recipe.app.common.response.BaseResponseStatus.NOT_FOUND_INGREDIENT;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FridgeService {

    private static final String FIREBASE_API_URL = "https://fcm.googleapis.com/fcm/send";
    private final FridgeRepository fridgeRepository;
    private final FridgeBasketJpaRepository fridgeBasketRepository;
    private final RecipeInfoRepository recipeInfoRepository;
    private final UserRepository userRepository;
    private final FridgeBasketService fridgeBasketService;

    @Transactional
    public List<Fridge> createFridges(User user) {

        List<FridgeBasket> fridgeBaskets = fridgeBasketService.getFridgeBasketsByUser(user);
        List<Fridge> fridges = fridgeBaskets.stream()
                .map(Fridge::from)
                .collect(Collectors.toList());

        return fridgeRepository.saveAll(fridges);
    }

    public List<Fridge> getFridges(User user) {
        return fridgeRepository.findByUser(user);
    }

    @Transactional
    public List<Fridge> deleteFridge(User user, Long fridgeId) {

        Fridge fridge = fridgeRepository.findByUserAndFridgeId(user, fridgeId).orElseThrow(NotFoundFridgeException::new);
        fridgeRepository.delete(fridge);

        return getFridges(user);
    }

    @Transactional
    public List<Fridge> updateFridge(User user, Long fridgeId, FridgeDto.FridgeRequest request) {

        Fridge fridge = fridgeRepository.findByUserAndFridgeId(user, fridgeId).orElseThrow(NotFoundFridgeException::new);
        fridge = fridge.changeExpiredAtAndQuantityAndUnit(request.getExpiredAt(), request.getQuantity(), request.getUnit());
        fridgeRepository.save(fridge);

        return getFridges(user);
    }

    @Transactional
    public void updateFcmToken(PatchFcmTokenReq patchFcmTokenReq, User user) {
        String fcmToken = patchFcmTokenReq.getFcmToken();
        user = user.changeDeviceToken(fcmToken);
        userRepository.save(user);
    }

    public List<RecipeInfo> retrieveFridgeRecipes(User user, Integer start, Integer display) {
        return recipeInfoRepository.searchRecipeListOrderByIngredientCntWhichUserHasDesc(UserEntity.fromModel(user), "ACTIVE").stream()
                .skip(start == 0 ? 0 : start - 1)
                .limit(display)
                .collect(Collectors.toList());
    }

    public int countFridgeRecipes(User user) {
        return recipeInfoRepository.searchRecipeListOrderByIngredientCntWhichUserHasDesc(UserEntity.fromModel(user), "ACTIVE").size();
    }

    public List<ShelfLifeUser> retreiveShelfLifeUserList() throws BaseException {
        SimpleDateFormat sdFormat = new SimpleDateFormat("yy.MM.dd");
        String today = sdFormat.format(new Date());

        List<FridgeEntity> fridgeEntityList = fridgeRepository.findAllByStatusAnd3DaysBeforeExpiredAt("ACTIVE", today);

        List<ShelfLifeUser> shelfLifeUsers = new ArrayList<>();
        for (FridgeEntity fridgeEntity : fridgeEntityList) {
            String deviceToken = fridgeEntity.getUser().getDeviceToken();
            String ingredientName = fridgeEntity.getIngredient().getIngredientName();
            ShelfLifeUser shelfLifeUser = new ShelfLifeUser(deviceToken, ingredientName);
            shelfLifeUsers.add(shelfLifeUser);
        }
        return shelfLifeUsers;
    }
}
