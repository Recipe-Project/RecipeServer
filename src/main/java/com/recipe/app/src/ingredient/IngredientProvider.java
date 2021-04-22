package com.recipe.app.src.ingredient;

import com.recipe.app.config.BaseException;
import com.recipe.app.src.fridgeBasket.FridgeBasketRepository;
import com.recipe.app.src.ingredient.models.GetIngredientsRes;
import com.recipe.app.src.ingredient.models.Ingredient;
import com.recipe.app.src.ingredient.models.IngredientList;
import com.recipe.app.src.ingredient.models.Ingredients;
import com.recipe.app.src.ingredientCategory.IngredientCategoryProvider;
import com.recipe.app.src.ingredientCategory.IngredientCategoryRepository;
import com.recipe.app.src.ingredientCategory.models.IngredientCategory;
import com.recipe.app.src.user.UserProvider;
import com.recipe.app.src.user.models.User;
import com.recipe.app.utils.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.recipe.app.config.BaseResponseStatus.*;


@Service
public class IngredientProvider {
    private final UserProvider userProvider;
    private final IngredientCategoryProvider ingredientCategoryProvider;
    private final IngredientRepository ingredientRepository;
    private final IngredientCategoryRepository ingredientCategoryRepository;
    private final FridgeBasketRepository fridgeBasketRepository;
    private final JwtService jwtService;

    @Autowired
    public IngredientProvider(UserProvider userProvider, IngredientCategoryProvider ingredientCategoryProvider, IngredientRepository ingredientRepository, IngredientCategoryRepository ingredientCategoryRepository, FridgeBasketRepository fridgeBasketRepository, JwtService jwtService) {
        this.userProvider = userProvider;
        this.ingredientCategoryProvider = ingredientCategoryProvider;
        this.ingredientRepository = ingredientRepository;
        this.ingredientCategoryRepository = ingredientCategoryRepository;
        this.fridgeBasketRepository = fridgeBasketRepository;
        this.jwtService = jwtService;
    }


    /**
     * 재료 조회 API - 키워드 있는경우
     *
     * @param keyword,userIdx
     * @return GetIngredientsRes
     * @throws BaseException
     */
    @Transactional
    public GetIngredientsRes retrieveKeywordIngredientsList(String keyword,Integer userIdx) throws BaseException {
        User user = userProvider.retrieveUserByUserIdx(userIdx);
        long fridgeBasketCount;
        try {
            fridgeBasketCount = fridgeBasketRepository.countByUserAndStatus(user,"ACTIVE");
        } catch (Exception ignored) {
            throw new BaseException(FAILED_TO_GET_FRIDGE_BASKET_COUNT);
        }

        List<IngredientCategory> ingredientCategories;

        try {
            ingredientCategories = ingredientCategoryRepository.findByStatus("ACTIVE");
        } catch (Exception ignored) {
            throw new BaseException(FAILED_TO_GET_INGREDIENT_CATEGORY);
        }

        List<Ingredients> ingredients = new ArrayList<>();
        for (int i=0;i<ingredientCategories.size()-1;i++){
            int ingredientCategoryIdx = ingredientCategories.get(i).getIngredientCategoryIdx();
            String ingredientCategoryName = ingredientCategories.get(i).getName();

            List<IngredientList> ingredientList = retrieveKeywordIngredients(ingredientCategoryIdx,keyword);

            Ingredients ingredient = new Ingredients(ingredientCategoryIdx, ingredientCategoryName, ingredientList);
            ingredients.add(ingredient);
        }

        return new GetIngredientsRes(fridgeBasketCount,ingredients);
    }

    /**
     * 재료 조회 API - 키워드 없는경우
     *
     * @param userIdx
     * @return GetIngredientsRes
     * @throws BaseException
     */
    @Transactional
    public GetIngredientsRes retrieveIngredientsList(Integer userIdx) throws BaseException {
        User user = userProvider.retrieveUserByUserIdx(userIdx);
        long fridgeBasketCount;
        try {
            fridgeBasketCount = fridgeBasketRepository.countByUserAndStatus(user,"ACTIVE");
        } catch (Exception ignored) {
            throw new BaseException(FAILED_TO_GET_FRIDGE_BASKET_COUNT);
        }

        List<IngredientCategory> ingredientCategories;

        try {
            // 카테고리 리스트 뽑기
            ingredientCategories = ingredientCategoryRepository.findByStatus("ACTIVE");
        } catch (Exception ignored) {
            throw new BaseException(FAILED_TO_GET_INGREDIENT_CATEGORY);
        }

        List<Ingredients> ingredients = new ArrayList<>();
        for (int i=0;i<ingredientCategories.size()-1;i++){
            int ingredientCategoryIdx = ingredientCategories.get(i).getIngredientCategoryIdx();
            String ingredientCategoryName = ingredientCategories.get(i).getName();

            List<IngredientList> ingredientList = retrieveIngredients(ingredientCategoryIdx);

            Ingredients ingredient = new Ingredients(ingredientCategoryIdx, ingredientCategoryName, ingredientList);
            ingredients.add(ingredient);
        }

        return new GetIngredientsRes(fridgeBasketCount,ingredients);
    }
    /**
     * 카테고리에 해당하는 재료리스트 추출
     *
     * @param ingredientCategoryIdx
     * @return List<IngredientList>
     * @throws BaseException
     */
    public List<IngredientList> retrieveIngredients(Integer ingredientCategoryIdx) throws BaseException {
        IngredientCategory ingredientCategory = ingredientCategoryProvider.retrieveIngredientCategoryByIngredientCategoryIdx(ingredientCategoryIdx);

        List<Ingredient> ingredients;
        try {
            ingredients = ingredientRepository.findByIngredientCategoryAndStatus(ingredientCategory,"ACTIVE");
        } catch (Exception ignored) {
            throw new BaseException(FAILED_TO_GET_INGREDIENT_LIST);
        }

        // 카테고리에 해당하는 재료 리스트 생성
        return ingredients.stream().map(ing -> {

            Integer ingredientIdx = ing.getIngredientIdx();
            String ingredientName = ing.getName();
            String ingredientIcon = ing.getIcon();

            return new IngredientList(ingredientIdx, ingredientName, ingredientIcon);

        }).collect(Collectors.toList());
    }

    /**
     * 카테고리에 해당하는+키워드에 해당하는 재료리스트 추출
     *
     * @param ingredientCategoryIdx
     * @return List<IngredientList>
     * @throws BaseException
     */
    public List<IngredientList> retrieveKeywordIngredients(Integer ingredientCategoryIdx,String keyword) throws BaseException {
        IngredientCategory ingredientCategory = ingredientCategoryProvider.retrieveIngredientCategoryByIngredientCategoryIdx(ingredientCategoryIdx);
        List<Ingredient> ingredients;


        try {
            ingredients = ingredientRepository.findByNameContainingAndIngredientCategoryAndStatus(keyword,ingredientCategory,"ACTIVE");
        } catch (Exception ignored) {
            throw new BaseException(FAILED_TO_GET_INGREDIENT_LIST);
        }

        return ingredients.stream().map(ing -> {

            Integer ingredientIdx = ing.getIngredientIdx();
            String ingredientName = ing.getName();
            String ingredientIcon = ing.getIcon();

            return new IngredientList(ingredientIdx, ingredientName,ingredientIcon);

        }).collect(Collectors.toList());
    }

    /**
     * Ingredient에서 재료명으로 재료 조회하기
     *
     * @param
     * @return FridgeBasket
     * @throws BaseException
     */
    public Ingredient retreiveIngredientByName(String name) throws BaseException {
        Ingredient ingredient;
        try {
            ingredient = ingredientRepository.findByNameAndStatus(name,"ACTIVE");
        } catch (Exception ignored) {
            throw new BaseException(FAILED_TO_RETREIVE_INGREDIENT_BY_NAME);
        }

        return ingredient;
    }


    /**
     * ingredientIdx로 재료 조회
     *
     * @param ingredientIdx
     * @return ingredient
     * @throws BaseException
     */
    public Ingredient retrieveIngredientByIngredientIdx(Integer ingredientIdx) throws BaseException {
        Ingredient ingredient;

        try {
            ingredient = ingredientRepository.findById(ingredientIdx).orElse(null);
        } catch (Exception ignored) {
            throw new BaseException(FAILED_TO_GET_INGREDIENT);
        }

        if (ingredient == null || !ingredient.getStatus().equals("ACTIVE")) {
            throw new BaseException(NOT_FOUND_INGREDIENT);
        }

        return ingredient;
    }

    /**
     * ingredientIdx로 재료 존재 여부 확인
     *
     * @param ingredientIdx
     * @return existIngredient
     * @throws BaseException
     */
    public Boolean existIngredient(Integer ingredientIdx) throws BaseException {
        Boolean existIngredient;

        try {
            existIngredient = ingredientRepository.existsByIngredientIdxAndStatus(ingredientIdx,"ACTIVE");
        } catch (Exception ignored) {
            throw new BaseException(FAILED_TO_GET_INGREDIENT);
        }

        return existIngredient;
    }
}