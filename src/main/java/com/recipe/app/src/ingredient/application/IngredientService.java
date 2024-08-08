package com.recipe.app.src.ingredient.application;

import com.recipe.app.src.ingredient.application.dto.IngredientCreateResponse;
import com.recipe.app.src.ingredient.application.dto.IngredientRequest;
import com.recipe.app.src.ingredient.domain.Ingredient;
import com.recipe.app.src.ingredient.domain.IngredientCategory;
import com.recipe.app.src.ingredient.exception.NotFoundIngredientException;
import com.recipe.app.src.ingredient.infra.IngredientRepository;
import com.recipe.app.src.user.domain.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;

@Service
public class IngredientService {

    private final IngredientCategoryService ingredientCategoryService;
    private final IngredientRepository ingredientRepository;

    public IngredientService(IngredientRepository ingredientRepository, IngredientCategoryService ingredientCategoryService) {
        this.ingredientRepository = ingredientRepository;
        this.ingredientCategoryService = ingredientCategoryService;
    }

    @Transactional(readOnly = true)
    public List<Ingredient> findByKeyword(Long userId, String keyword) {

        return ingredientRepository.findDefaultIngredientsByKeyword(userId, keyword);
    }

    @Transactional(readOnly = true)
    public List<Ingredient> findByIngredientIds(Collection<Long> ingredientIds) {

        return ingredientRepository.findByIngredientIdIn(ingredientIds);
    }

    @Transactional
    public IngredientCreateResponse create(Long userId, IngredientRequest request) {

        IngredientCategory ingredientCategory = ingredientCategoryService.findById(request.getIngredientCategoryId());

        Ingredient ingredient = ingredientRepository.findByUserIdAndIngredientNameAndIngredientIconIdAndIngredientCategoryId(
                        userId,
                        request.getIngredientName(),
                        request.getIngredientIconId(),
                        ingredientCategory.getIngredientCategoryId())
                .orElseGet(() -> ingredientRepository.save(request.toEntity(userId)));

        return new IngredientCreateResponse(ingredient.getIngredientId());
    }

    @Transactional
    public void deleteAllByUserId(long userId) {

        List<Ingredient> ingredients = ingredientRepository.findByUserId(userId);

        ingredientRepository.deleteAll(ingredients);
    }

    @Transactional(readOnly = true)
    public Ingredient findByIngredientId(Long ingredientId) {

        return ingredientRepository.findById(ingredientId).orElseThrow(() -> {
            throw new NotFoundIngredientException();
        });
    }

    @Transactional
    public void delete(User user, Long ingredientId) {

        ingredientRepository.findByIngredientIdAndUserId(ingredientId, user.getUserId())
                .ifPresent(ingredientRepository::delete);
    }

    @Transactional(readOnly = true)
    public List<Ingredient> findByUserId(Long userId) {

        return ingredientRepository.findByUserId(userId);
    }
}
