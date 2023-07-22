package com.recipe.app.src.fridgeBasket.domain;

import com.recipe.app.common.entity.BaseEntity;
import com.recipe.app.common.exception.BaseException;
import com.recipe.app.src.ingredient.domain.Ingredient;
import com.recipe.app.src.ingredient.domain.IngredientCategory;
import com.recipe.app.src.user.domain.User;
import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.util.StringUtils;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static com.recipe.app.common.response.BaseResponseStatus.*;

@NoArgsConstructor(access = AccessLevel.PUBLIC)
@EqualsAndHashCode(callSuper = false)
@Data
@Entity
@Table(name = "FridgeBasket")
public class FridgeBasket extends BaseEntity {
    @Id
    @Column(name = "idx", nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idx;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userIdx", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ingredientIdx")
    private Ingredient ingredient;

    @Column(name = "ingredientName", nullable = false, length = 45)
    private String ingredientName;

    @Column(name = "ingredientIcon")
    private String ingredientIcon;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ingredientCategoryIdx", nullable = false)
    private IngredientCategory ingredientCategory;

    @Column(name = "count")
    private Integer count = 1;

    @Column(name = "storageMethod", nullable = false, length = 4)
    private String storageMethod = "냉장";

    @Column(name = "expiredAt")
    private LocalDate expiredAt;

    @Column(name = "status", nullable = false, length = 10)
    private String status = "ACTIVE";

    public FridgeBasket(User user, Ingredient ingredient, String ingredientName, String ingredientIcon, IngredientCategory ingredientCategory) {
        if (StringUtils.hasText(ingredientName)) {
            throw new BaseException(POST_FRIDGES_DIRECT_BASKET_EMPTY_INGREDIENT_NAME);
        }
        if (StringUtils.hasText(ingredientIcon)) {
            throw new BaseException(POST_FRIDGES_DIRECT_BASKET_EMPTY_INGREDIENT_ICON);
        }
        if (ingredientCategory == null) {
            throw new BaseException(POST_FRIDGES_DIRECT_BASKET_EMPTY_INGREDIENT_CATEGORY_IDX);
        }

        this.user = user;
        this.ingredient = ingredient;
        this.ingredientName = ingredientName;
        this.ingredientIcon = ingredientIcon;
        this.ingredientCategory = ingredientCategory;
    }

    public void setExpiredAt(String expiredAt) {
        this.expiredAt = LocalDate.parse(expiredAt, DateTimeFormatter.ofPattern("yyyy.MM.dd"));
    }
}