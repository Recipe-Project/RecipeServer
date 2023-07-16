package com.recipe.app.src.scrap.application;

import com.recipe.app.common.exception.BaseException;
import com.recipe.app.src.recipe.domain.RecipeInfo;
import com.recipe.app.src.recipe.mapper.RecipeInfoRepository;
import com.recipe.app.src.scrap.domain.ScrapPublic;
import com.recipe.app.src.scrap.mapper.ScrapPublicRepository;
import com.recipe.app.src.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.recipe.app.common.response.BaseResponseStatus.NOT_FOUND_RECIPE_INFO;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ScrapPublicService {

    private final ScrapPublicRepository scrapPublicRepository;
    private final RecipeInfoRepository recipeInfoRepository;

    @Transactional
    public void createOrDeleteScrapRecipe(int recipeId, User user) {
        RecipeInfo recipeInfo = recipeInfoRepository.findById(recipeId).orElseThrow(() -> {
            throw new BaseException(NOT_FOUND_RECIPE_INFO);
        });

        scrapPublicRepository.findByUserAndRecipeInfoAndStatus(user, recipeInfo, "ACTIVE")
                .ifPresentOrElse(scrapPublicRepository::delete, () -> {
                    scrapPublicRepository.save(new ScrapPublic(user, recipeInfo));
                });
    }

    public List<ScrapPublic> retrieveScrapRecipes(User user) {
        return scrapPublicRepository.findByUserAndStatusOrderByCreatedAtDesc(user, "ACTIVE");
    }

    public long countScrapPublicsByRecipe(RecipeInfo recipeInfo) {
        return scrapPublicRepository.countByRecipeInfoAndStatus(recipeInfo, "ACTIVE");
    }

    public long countScrapPublicsByUser(User user) {
        return scrapPublicRepository.countByUserAndStatus(user, "ACTIVE");
    }


}
