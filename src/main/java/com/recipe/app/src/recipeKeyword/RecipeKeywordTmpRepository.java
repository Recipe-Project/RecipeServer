package com.recipe.app.src.recipeKeyword;

import com.recipe.app.src.recipeKeyword.models.RecipeKeywordTmp;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RecipeKeywordTmpRepository extends CrudRepository<RecipeKeywordTmp, String> {
}
