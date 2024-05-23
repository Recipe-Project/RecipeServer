package com.recipe.app.src.ingredient.exception;

public class IngredientUsedInFridgeException extends RuntimeException {

    public IngredientUsedInFridgeException() {
        super("재료를 냉장고에서 사용하고 있습니다.\n재료 삭제를 위해서는 냉장고 내에서 삭제해주세요.");
    }
}
