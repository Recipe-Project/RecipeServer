package com.recipe.app.src.ingredient.infra;

import com.recipe.app.src.ingredient.domain.IngredientSynonym;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IngredientSynonymRepository extends JpaRepository<IngredientSynonym, Long> {
}
