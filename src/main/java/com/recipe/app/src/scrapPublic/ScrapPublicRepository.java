package com.recipe.app.src.scrapPublic;

import com.recipe.app.src.recipeInfo.models.RecipeInfo;
import com.recipe.app.src.scrapPublic.models.ScrapPublic;
import com.recipe.app.src.user.models.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface ScrapPublicRepository extends CrudRepository<ScrapPublic, Integer> {
    ScrapPublic findByUserAndRecipeInfoAndStatus( User user,RecipeInfo recipeInfo, String status);
    long countByUserAndStatus(User user,String status);
    List<ScrapPublic> findByUserAndStatus(User user, String status);
    List<ScrapPublic> findByUserAndStatus(User user, String status, Sort sort);
    long countByRecipeInfoAndStatus(RecipeInfo recipeInfo,String status);
}
