package com.recipe.app.src.recipe.infra;

import com.recipe.app.src.recipe.domain.RecipeProcess;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface RecipeProcessRepository extends JpaRepository<RecipeProcess, Long> {

    List<RecipeProcess> findByRecipeIdOrderByCookingNo(Long recipeId);

    List<RecipeProcess> findByRecipeIdIn(Collection<Long> recipeIds);

    List<RecipeProcess> findByRecipeId(Long recipeId);
}
