package com.recipe.app.src.recipe.infra;

import com.recipe.app.common.entity.BaseEntity;
import com.recipe.app.src.ingredient.infra.IngredientEntity;
import com.recipe.app.src.recipe.domain.RecipeIngredient;
import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@NoArgsConstructor(access = AccessLevel.PUBLIC)
@EqualsAndHashCode(callSuper = false)
@Data
@Entity
@Table(name = "RecipeIngredient")
public class RecipeIngredientEntity extends BaseEntity {

    @Id
    @Column(name = "recipeIngredientId", nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long recipeIngredientId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recipeId", nullable = false)
    private RecipeEntity recipe;

    @ManyToOne
    @JoinColumn(name = "ingredientId", nullable = false)
    private IngredientEntity ingredient;

    @Column(name = "capacity")
    private String capacity;

    public static RecipeIngredientEntity fromModel(RecipeIngredient recipeIngredient) {
        RecipeIngredientEntity recipeIngredientEntity = new RecipeIngredientEntity();
        recipeIngredientEntity.recipeIngredientId = recipeIngredient.getRecipeIngredientId();
        recipeIngredientEntity.recipe = RecipeEntity.fromModel(recipeIngredient.getRecipe());
        recipeIngredientEntity.ingredient = IngredientEntity.fromModel(recipeIngredient.getIngredient());
        recipeIngredientEntity.capacity = recipeIngredient.getCapacity();
        return recipeIngredientEntity;
    }

    public RecipeIngredient toModel() {
        return RecipeIngredient.builder()
                .recipeIngredientId(recipeIngredientId)
                .recipe(recipe.toModel())
                .ingredient(ingredient.toModel())
                .capacity(capacity)
                .build();
    }
}
