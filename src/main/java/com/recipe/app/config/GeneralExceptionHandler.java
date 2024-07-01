package com.recipe.app.config;

import com.recipe.app.src.common.exception.NotFoundNoticeException;
import com.recipe.app.src.fridge.exception.NotFoundFridgeException;
import com.recipe.app.src.fridgeBasket.exception.NotFoundFridgeBasketException;
import com.recipe.app.src.ingredient.exception.NotFoundIngredientCategoryException;
import com.recipe.app.src.ingredient.exception.NotFoundIngredientException;
import com.recipe.app.src.recipe.exception.NotFoundRecipeException;
import com.recipe.app.src.recipe.exception.NotFoundRecipeLevelException;
import com.recipe.app.src.user.exception.NotFoundUserException;
import com.recipe.app.src.user.exception.UserTokenNotExistException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GeneralExceptionHandler {

    private final Logger log = LoggerFactory.getLogger(getClass());

    @ExceptionHandler({NotFoundFridgeException.class, NotFoundIngredientException.class, NotFoundFridgeBasketException.class,
            NotFoundIngredientCategoryException.class, NotFoundNoticeException.class, NotFoundRecipeLevelException.class,
            NotFoundRecipeException.class, NotFoundUserException.class})
    public ResponseEntity<?> handleNotFoundException(Exception e) {
        log.error(e.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e);
    }

    @ExceptionHandler({UserTokenNotExistException.class})
    public ResponseEntity<?> handleUnauthorizedException(Exception e) {
        log.error(e.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e);
    }
}
