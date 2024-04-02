package com.recipe.app.src.ingredient.domain;

import com.google.common.base.Preconditions;
import com.recipe.app.common.entity.BaseEntity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.util.StringUtils;

import javax.persistence.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "IngredientCategory")
public class IngredientCategory extends BaseEntity {

    @Id
    @Column(name = "ingredientCategoryId", nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long ingredientCategoryId;

    @Column(name = "ingredientCategoryName", nullable = false, length = 10)
    private String ingredientCategoryName;

    @Builder
    public IngredientCategory(Long ingredientCategoryId, String ingredientCategoryName) {

        Preconditions.checkArgument(StringUtils.hasText(ingredientCategoryName), "재료 카테고리명을 입력해주세요.");

        this.ingredientCategoryId = ingredientCategoryId;
        this.ingredientCategoryName = ingredientCategoryName;
    }
}