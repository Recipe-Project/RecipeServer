package com.recipe.app.src.recipe.infra;

import com.recipe.app.src.recipe.domain.RecipeScrap;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface RecipeScrapRepository extends JpaRepository<RecipeScrap, Long> {

    Optional<RecipeScrap> findByUserIdAndRecipeId(Long userId, Long recipeId);

    long countByUserId(Long userId);

    List<RecipeScrap> findByRecipeId(Long recipeId);

    List<RecipeScrap> findByRecipeIdIn(Collection<Long> recipeIds);

    long countByRecipeId(Long recipeId);

    List<RecipeScrap> findByUserId(Long userId);
}