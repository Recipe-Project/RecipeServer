package com.recipe.app.src.recipeProcess;

import com.recipe.app.src.recipeProcess.models.RecipeProcess;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface RecipeProcessRepository extends CrudRepository<RecipeProcess, Integer> {
}
