package com.recipe.app.src.fridgeBasket;

import com.recipe.app.config.BaseException;
import com.recipe.app.src.fridgeBasket.models.FridgeBasket;
import com.recipe.app.src.ingredient.models.GetIngredientsRes;
import com.recipe.app.src.ingredient.models.IngredientList;
import com.recipe.app.src.ingredientCategory.models.IngredientCategory;
import com.recipe.app.src.user.UserProvider;
import com.recipe.app.utils.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static com.recipe.app.config.BaseResponseStatus.*;

@Service
public class FridgeBasketProvider {
    private final UserProvider userProvider;
    private final FridgeBasketRepository fridgeBasketRepository;
    private final JwtService jwtService;

    @Autowired
    public FridgeBasketProvider(UserProvider userProvider, FridgeBasketRepository fridgeBasketRepository,JwtService jwtService) {
        this.userProvider = userProvider;
        this.fridgeBasketRepository = fridgeBasketRepository;
        this.jwtService = jwtService;
    }

    /**
     * FridgeBasket에서 재료명으로 재료 조회하기
     *
     * @param
     * @return FridgeBasket
     * @throws BaseException
     */
    public FridgeBasket retreiveFridgeBasketByName(String name) throws BaseException {
        FridgeBasket fridgeBasket;
        try {
            fridgeBasket = fridgeBasketRepository.findByIngredientNameAndStatus(name,"ACTIVE");
        } catch (Exception ignored) {
            throw new BaseException(FAILED_TO_RETREIVE_FRIDGE_BASKET_BY_NAME);
        }

        return fridgeBasket;
    }
}
