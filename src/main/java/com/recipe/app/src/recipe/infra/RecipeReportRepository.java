package com.recipe.app.src.recipe.infra;

import com.recipe.app.src.recipe.domain.RecipeReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RecipeReportRepository extends JpaRepository<RecipeReport, Long> {

    Optional<RecipeReport> findByUserIdAndRecipeId(Long userId, Long recipeId);

    long countByRecipeId(Long recipeId);
}
