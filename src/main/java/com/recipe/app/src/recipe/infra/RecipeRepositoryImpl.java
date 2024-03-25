package com.recipe.app.src.recipe.infra;

import com.recipe.app.src.ingredient.domain.Ingredient;
import com.recipe.app.src.recipe.application.port.RecipeRepository;
import com.recipe.app.src.recipe.domain.Recipe;
import com.recipe.app.src.recipe.domain.RecipeIngredient;
import com.recipe.app.src.recipe.domain.RecipeProcess;
import com.recipe.app.src.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
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
    public Page<Recipe> getRecipesOrderByCreatedAtDesc(String keyword, Pageable pageable) {
        return recipeJpaRepository.getRecipesOrderByCreatedAtDesc(keyword, pageable).map(RecipeEntity::toModel);
    }

    @Override
    public Page<Recipe> getRecipesOrderByRecipeScrapSizeDesc(String keyword, Pageable pageable) {
        return recipeJpaRepository.getRecipesOrderByRecipeScrapSizeDesc(keyword, pageable).map(RecipeEntity::toModel);
    }

    @Override
    public Page<Recipe> getRecipesOrderByRecipeViewSizeDesc(String keyword, Pageable pageable) {
        return recipeJpaRepository.getRecipesOrderByRecipeViewSizeDesc(keyword, pageable).map(RecipeEntity::toModel);
    }

    @Override
    public void saveRecipeScrap(Recipe recipe, User user) {
        recipeScrapJpaRepository.findByUserAndRecipe(User.fromModel(user), RecipeEntity.fromModel(recipe))
                .orElseGet(() -> recipeScrapJpaRepository.save(RecipeScrapEntity.create(user, recipe)));
    }

    @Override
    public void deleteRecipeScrap(Recipe recipe, User user) {
        recipeScrapJpaRepository.findByUserAndRecipe(User.fromModel(user), RecipeEntity.fromModel(recipe))
                .ifPresent(recipeScrapJpaRepository::delete);

    }

    @Override
    public Page<Recipe> findScrapRecipesByUser(User user, Pageable pageable) {
        return recipeScrapJpaRepository.findByUser(User.fromModel(user), pageable)
                .map(RecipeScrapEntity::getRecipe)
                .map(RecipeEntity::toModel);
    }

    @Override
    public void saveRecipeView(Recipe recipe, User user) {
        recipeViewJpaRepository.findByUserAndRecipe(User.fromModel(user), RecipeEntity.fromModel(recipe))
                .orElseGet(() -> recipeViewJpaRepository.save(RecipeViewEntity.create(user, recipe)));
    }

    @Override
    public Page<Recipe> findByUser(User user, Pageable pageable) {
        return recipeJpaRepository.findByUser(User.fromModel(user), pageable).map(RecipeEntity::toModel);
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
    public List<RecipeProcess> findRecipeProcessesByRecipe(Recipe recipe) {
        return recipeProcessJpaRepository.findByRecipeOrderByCookingNo(RecipeEntity.fromModel(recipe)).stream()
                .map(RecipeProcessEntity::toModel)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteRecipeIngredients(List<RecipeIngredient> recipeIngredients) {
        recipeIngredientJpaRepository.deleteAll(recipeIngredients.stream()
                .map(RecipeIngredientEntity::fromModel)
                .collect(Collectors.toList()));
    }

    @Override
    public Page<Recipe> findRecipesOrderByFridgeIngredientCntDesc(List<Ingredient> ingredients, List<String> ingredientNames, User user, Pageable pageable) {
        return recipeJpaRepository.findRecipesOrderByFridgeIngredientCntDesc(ingredients.stream()
                        .map(Ingredient::getIngredientId)
                        .collect(Collectors.toList()), ingredientNames, user.getUserId(), pageable)
                .map(recipeEntityWithRate -> {
                    Recipe recipe = new RecipeEntity(recipeEntityWithRate).toModel();
                    recipe.addScrapUser(recipeEntityWithRate.getScrapUserId());
                    recipe.addViewUser(recipeEntityWithRate.getViewUserId());
                    return recipe;
                });
    }

    @Override
    public long countRecipeScrapByUser(User user) {
        return recipeScrapJpaRepository.countByUser(User.fromModel(user));
    }

    @Override
    public List<RecipeIngredient> findRecipeIngredientsByRecipe(Recipe recipe) {
        return recipeIngredientJpaRepository.findByRecipe(RecipeEntity.fromModel(recipe)).stream()
                .map(RecipeIngredientEntity::toModel)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteRecipeProcesses(List<RecipeProcess> recipeProcesses) {
        recipeProcessJpaRepository.deleteAll(recipeProcesses.stream()
                .map(RecipeProcessEntity::fromModel)
                .collect(Collectors.toList()));
    }

    @Override
    public List<RecipeIngredient> findRecipeIngredientsByRecipeIn(List<Recipe> recipes) {
        return recipeIngredientJpaRepository.findByRecipeIn(recipes.stream()
                        .map(RecipeEntity::fromModel)
                        .collect(Collectors.toList())).stream()
                .map(RecipeIngredientEntity::toModel)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteRecipeScrapsByRecipe(Recipe recipe) {
        recipeScrapJpaRepository.deleteAll(recipeScrapJpaRepository.findByRecipe(RecipeEntity.fromModel(recipe)));
    }

    @Override
    public void deleteRecipeViewsByRecipe(Recipe recipe) {
        recipeViewJpaRepository.deleteAll(recipeViewJpaRepository.findByRecipe(RecipeEntity.fromModel(recipe)));
    }
}
