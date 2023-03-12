package com.recipe.app.src.ingredientCategory;

import com.recipe.app.common.exception.BaseException;
import com.recipe.app.src.ingredientCategory.models.IngredientCategory;
import com.recipe.app.common.utils.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.recipe.app.common.response.BaseResponseStatus.*;

@Service
public class IngredientCategoryProvider {
    private final IngredientCategoryRepository ingredientCategoryRepository;
    private final JwtService jwtService;

    @Autowired
    public IngredientCategoryProvider(IngredientCategoryRepository ingredientCategoryRepository, JwtService jwtService) {
        this.ingredientCategoryRepository = ingredientCategoryRepository;
        this.jwtService = jwtService;
    }
    /**
     * Idx로 카테고리 조회
     *
     * @param ingredientCategoryIdx
     * @return IngredientCategory
     * @throws BaseException
     */
    public IngredientCategory retrieveIngredientCategoryByIngredientCategoryIdx(Integer ingredientCategoryIdx) throws BaseException {
        IngredientCategory ingredientCategory = ingredientCategoryRepository.findById(ingredientCategoryIdx).orElse(null);

        if (ingredientCategory == null || !ingredientCategory.getStatus().equals("ACTIVE")) {
            throw new BaseException(NOT_FOUND_INGREDIENT_CATEGORY);
        }

        return ingredientCategory;
    }
}
