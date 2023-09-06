package com.recipe.app.src.recipe.infra;

import com.recipe.app.src.ingredient.domain.Ingredient;
import com.recipe.app.src.ingredient.infra.IngredientEntity;
import com.recipe.app.src.recipe.application.port.RecipeRepository;
import com.recipe.app.src.recipe.domain.Recipe;
import com.recipe.app.src.recipe.domain.RecipeIngredient;
import com.recipe.app.src.recipe.domain.RecipeProcess;
import com.recipe.app.src.user.domain.User;
import com.recipe.app.src.user.infra.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Repository
@RequiredArgsConstructor
public class RecipeRepositoryImpl implements RecipeRepository {

    private final RecipeJpaRepository recipeJpaRepository;
    private final RecipeProcessJpaRepository recipeProcessJpaRepository;
    private final RecipeIngredientJpaRepository recipeIngredientJpaRepository;
    private final RecipeScrapJpaRepository recipeScrapJpaRepository;
    private final RecipeViewJpaRepository recipeViewJpaRepository;

    @Override
    public Optional<Recipe> findById(Long recipeId) {
        return recipeJpaRepository.findById(recipeId).map(RecipeEntity::toModel);
    }

    @Override
    public List<Recipe> getRecipes(String keyword) {
        return recipeJpaRepository.getRecipes(keyword).stream().map(RecipeEntity::toModel).collect(Collectors.toList());
    }

    @Override
    public void saveRecipeScrap(Recipe recipe, User user) {
        recipeScrapJpaRepository.findByUserAndRecipe(UserEntity.fromModel(user), RecipeEntity.fromModel(recipe))
                .orElseGet(() -> recipeScrapJpaRepository.save(RecipeScrapEntity.create(user, recipe)));
    }

    @Override
    public void deleteRecipeScrap(Recipe recipe, User user) {
        recipeScrapJpaRepository.findByUserAndRecipe(UserEntity.fromModel(user), RecipeEntity.fromModel(recipe))
                .ifPresent(recipeScrapJpaRepository::delete);

    }

    @Override
    public List<Recipe> findScrapRecipesByUser(User user) {
        return recipeScrapJpaRepository.findByUser(UserEntity.fromModel(user)).stream()
                .map(RecipeScrapEntity::getRecipe)
                .map(RecipeEntity::toModel)
                .collect(Collectors.toList());
    }

    @Override
    public void saveRecipeView(Recipe recipe, User user) {
        recipeViewJpaRepository.findByUserAndRecipe(UserEntity.fromModel(user), RecipeEntity.fromModel(recipe))
                .orElseGet(() -> recipeViewJpaRepository.save(RecipeViewEntity.create(user, recipe)));
    }

    @Override
    public List<Recipe> findByUser(User user) {
        return recipeJpaRepository.findByUser(UserEntity.fromModel(user)).stream()
                .map(RecipeEntity::toModel)
                .collect(Collectors.toList());
    }

    @Override
    public void delete(Recipe recipe) {
        recipeJpaRepository.delete(RecipeEntity.fromModel(recipe));
    }

    @Override
    public Recipe save(Recipe recipe) {
        return recipeJpaRepository.save(RecipeEntity.fromModel(recipe)).toModel();
    }

    @Override
    public void saveRecipeProcess(RecipeProcess recipeProcess) {
        recipeProcessJpaRepository.save(RecipeProcessEntity.fromModel(recipeProcess)).toModel();
    }

    @Override
    public void saveRecipeIngredients(List<RecipeIngredient> recipeIngredients) {
        StreamSupport.stream(recipeIngredientJpaRepository.saveAll(recipeIngredients.stream()
                        .map(RecipeIngredientEntity::fromModel)
                        .collect(Collectors.toList())).spliterator(), false)
                .map(RecipeIngredientEntity::toModel)
                .collect(Collectors.toList());
    }

    @Override
    public RecipeProcess findRecipeProcessByRecipe(Recipe recipe) {
        return recipeProcessJpaRepository.findByRecipeAndCookingNo(RecipeEntity.fromModel(recipe), 1).toModel();
    }

    @Override
    public void deleteRecipeIngredients(List<RecipeIngredient> recipeIngredients) {
        recipeIngredientJpaRepository.deleteAll(recipeIngredients.stream()
                .map(RecipeIngredientEntity::fromModel)
                .collect(Collectors.toList()));
    }

    @Override
    public List<Recipe> findRecipesOrderByFridgeIngredientCntDesc(List<Ingredient> ingredients, Pageable pageable) {
        return recipeJpaRepository.findRecipesOrderByFridgeIngredientCntDesc(ingredients.stream()
                        .map(IngredientEntity::fromModel)
                        .collect(Collectors.toList()), pageable).stream()
                .map(RecipeEntity::toModel)
                .collect(Collectors.toList());
    }
}
