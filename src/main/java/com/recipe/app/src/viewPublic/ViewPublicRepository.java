package com.recipe.app.src.viewPublic;

import com.recipe.app.src.recipeInfo.models.RecipeInfo;
import com.recipe.app.src.viewPublic.models.ViewPublic;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface ViewPublicRepository extends CrudRepository<ViewPublic, Integer> {
    long countByRecipeInfoAndStatus(RecipeInfo recipeInfo, String status);
}

