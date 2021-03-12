package com.recipe.app.src.recipeInfo;



import com.recipe.app.src.recipeInfo.models.RecipeInfo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface RecipeInfoRepository extends CrudRepository<RecipeInfo, Integer> {
}
