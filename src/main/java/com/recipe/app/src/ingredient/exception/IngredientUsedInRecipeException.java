package com.recipe.app.src.ingredient.exception;

public class IngredientUsedInRecipeException extends RuntimeException {

    public IngredientUsedInRecipeException() {
        super("재료를 레시피에서 사용하고 있습니다.\n재료 삭제를 위해서는 레시피 내에서 삭제해주세요.");
    }
}
