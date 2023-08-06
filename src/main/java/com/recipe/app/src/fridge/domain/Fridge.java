package com.recipe.app.src.fridge.domain;

import com.recipe.app.common.entity.BaseEntity;
import com.recipe.app.common.exception.BaseException;
import com.recipe.app.src.ingredient.infra.IngredientCategoryEntity;
import com.recipe.app.src.user.infra.UserEntity;
import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.util.StringUtils;

import javax.persistence.*;
import java.security.InvalidParameterException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

import static com.recipe.app.common.response.BaseResponseStatus.*;

@NoArgsConstructor(access = AccessLevel.PUBLIC)
@EqualsAndHashCode(callSuper = false)
@Data
@Entity
@Table(name = "Fridge")
public class Fridge extends BaseEntity {
    @Id
    @Column(name = "idx", nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idx;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userIdx", nullable = false)
    private UserEntity user;

    @Column(name = "ingredientName", nullable = false, length = 20)
    private String ingredientName;

    @Column(name = "ingredientIcon")
    private String ingredientIcon;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ingredientCategoryIdx", nullable = false)
    private IngredientCategoryEntity ingredientCategory;

    @Column(name = "storageMethod", nullable = false, length = 2)
    private String storageMethod;

    @Column(name = "expiredAt", nullable = false)
    private LocalDate expiredAt;

    @Column(name = "count", nullable = false)
    private int count;

    @Column(name = "status", nullable = false, length = 10)
    private String status = "ACTIVE";

    public Fridge(UserEntity user, String ingredientName, String ingredientIcon, IngredientCategoryEntity ingredientCategory, String storageMethod, String expiredAt, int count) {

        if (!StringUtils.hasText(ingredientName)) {
            throw new InvalidParameterException(FRIDGES_EMPTY_INGREDIENT_NAME.getMessage());
        }
        if (!StringUtils.hasText(ingredientIcon)) {
            throw new InvalidParameterException(FRIDGES_EMPTY_INGREDIENT_ICON.getMessage());
        }
        if (ingredientCategory == null) {
            throw new InvalidParameterException(POST_FRIDGES_DIRECT_BASKET_EMPTY_INGREDIENT_CATEGORY_IDX.getMessage());
        }
        if (!StringUtils.hasText(storageMethod)) {
            throw new InvalidParameterException(EMPTY_STORAGE_METHOD.getMessage());
        }
        if (count <= 0) {
            throw new BaseException(EMPTY_INGREDIENT_COUNT);
        }
        if (expiredAt == null) {
            throw new BaseException(INVALID_DATE);
        }
        if (!storageMethod.equals("냉장") && !storageMethod.equals("냉동") && !storageMethod.equals("실온")) {
            throw new BaseException(INVALID_STORAGE_METHOD);
        }

        this.user = user;
        this.ingredientName = ingredientName;
        this.ingredientIcon = ingredientIcon;
        this.ingredientCategory = ingredientCategory;
        this.storageMethod = storageMethod;
        this.expiredAt = LocalDate.parse(expiredAt, DateTimeFormatter.ofPattern("yyyy.MM.dd"));
        this.count = count;
    }

    public Integer getFreshness() {
        if (this.expiredAt == null) {
            return 555;
        }
        long diffDay = ChronoUnit.DAYS.between(LocalDate.now(), expiredAt);
        if (diffDay < 0) {
            return 444;
        }
        if (diffDay <= 3) {
            return 1;
        }
        if (diffDay <= 7) {
            return 2;
        }
        return 3;
    }

    public void setExpiredAt(String expiredAt) {
        this.expiredAt = LocalDate.parse(expiredAt, DateTimeFormatter.ofPattern("yyyy.MM.dd"));
    }

}