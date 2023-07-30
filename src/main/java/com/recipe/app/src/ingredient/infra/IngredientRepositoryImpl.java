package com.recipe.app.src.ingredient.infra;

import com.recipe.app.src.ingredient.application.port.IngredientRepository;
import com.recipe.app.src.ingredient.domain.Ingredient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class IngredientRepositoryImpl implements IngredientRepository {

    private final IngredientJpaRepository ingredientJpaRepository;

    @Override
    public List<Ingredient> findByNameContainingAndStatus(String name, String status) {
        return ingredientJpaRepository.findByNameContainingAndStatus(name, status).stream().map(IngredientEntity::toModel).collect(Collectors.toList());
    }

    @Override
    public Optional<Ingredient> findByNameAndStatus(String name, String status) {
        return ingredientJpaRepository.findByNameAndStatus(name, status).map(IngredientEntity::toModel);
    }

    @Override
    public List<Ingredient> findByStatus(String status) {
        return ingredientJpaRepository.findByStatus(status).stream().map(IngredientEntity::toModel).collect(Collectors.toList());
    }

    @Override
    public List<Ingredient> findAllByIngredientIdxIn(List<Integer> idxList) {
        return ingredientJpaRepository.findAllByIngredientIdxIn(idxList).stream().map(IngredientEntity::toModel).collect(Collectors.toList());
    }
}
