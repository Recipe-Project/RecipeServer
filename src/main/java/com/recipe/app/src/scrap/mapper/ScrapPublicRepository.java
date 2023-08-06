package com.recipe.app.src.scrap.mapper;

import com.recipe.app.src.recipe.domain.RecipeInfo;
import com.recipe.app.src.scrap.domain.ScrapPublic;
import com.recipe.app.src.user.infra.UserEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ScrapPublicRepository extends CrudRepository<ScrapPublic, Integer> {
    Optional<ScrapPublic> findByUserAndRecipeInfoAndStatus(UserEntity user, RecipeInfo recipeInfo, String status);

    long countByUserAndStatus(UserEntity user, String status);

    List<ScrapPublic> findByUserAndStatusOrderByCreatedAtDesc(UserEntity user, String status);

    long countByRecipeInfoAndStatus(RecipeInfo recipeInfo, String status);
}
