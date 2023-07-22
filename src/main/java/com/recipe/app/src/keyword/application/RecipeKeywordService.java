package com.recipe.app.src.keyword.application;

import com.recipe.app.common.exception.BaseException;
import com.recipe.app.src.keyword.domain.RecipeKeyword;
import com.recipe.app.src.keyword.mapper.RecipeKeywordRepository;
import com.recipe.app.src.keyword.models.GetRecipesBestKeywordRes;
import com.recipe.app.src.user.application.UserService;
import com.recipe.app.common.utils.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RecipeKeywordService {

    private final RecipeKeywordRepository recipeKeywordRepository;

    @Transactional
    public void createRecipeKeyword(String keyword) {
        recipeKeywordRepository.save(new RecipeKeyword(keyword));
    }

    public List<String> retrieveRecipesBestKeyword() throws BaseException {
        List<Object[]> bestKeywordList = recipeKeywordRepository.findByBestKeywordTop10();

        return bestKeywordList.stream().map(keyword -> String.valueOf(keyword[0].toString())).collect(Collectors.toList());
    }

}