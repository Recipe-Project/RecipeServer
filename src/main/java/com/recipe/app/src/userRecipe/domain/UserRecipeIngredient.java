package com.recipe.app.src.userRecipe.domain;

import com.recipe.app.common.entity.BaseEntity;
import com.recipe.app.common.exception.BaseException;
import com.recipe.app.src.ingredient.models.Ingredient;
import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.util.StringUtils;

import javax.persistence.*;

import java.security.InvalidParameterException;

import static com.recipe.app.common.response.BaseResponseStatus.EMPTY_INGREDIENT_ICON;
import static com.recipe.app.common.response.BaseResponseStatus.EMPTY_INGREDIENT_NAME;

@NoArgsConstructor(access = AccessLevel.PUBLIC)
@EqualsAndHashCode(callSuper = false)
@Data
@Entity
@Table(name = "UserRecipeIngredient")
public class UserRecipeIngredient extends BaseEntity {
    @Id
    @Column(name = "idx", nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idx;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userRecipeIdx", nullable = false)
    private UserRecipe userRecipe;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ingredientIdx")
    private Ingredient ingredient;

    @Column(name = "ingredientIcon", nullable = false)
    private String ingredientIcon;

    @Column(name = "ingredientName", nullable = false, length = 10)
    private String ingredientName;

    @Column(name = "status", nullable = false, length = 10)
    private String status = "ACTIVE";

    public UserRecipeIngredient(UserRecipe userRecipe, Ingredient ingredient, String ingredientIcon, String ingredientName) {
        if (!StringUtils.hasText(ingredientName)) {
            throw new InvalidParameterException(EMPTY_INGREDIENT_NAME.getMessage());
        }
        if (!StringUtils.hasText(ingredientIcon)) {
            throw new InvalidParameterException(EMPTY_INGREDIENT_ICON.getMessage());
        }
        this.userRecipe = userRecipe;
        this.ingredient = ingredient;
        this.ingredientIcon = ingredientIcon;
        this.ingredientName = ingredientName;
    }
}
