package com.recipe.app.src.ingredient.domain;

import com.google.common.base.Preconditions;
import com.recipe.app.src.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.util.StringUtils;

import java.util.Objects;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "Ingredient")
public class Ingredient extends BaseEntity {
    @Id
    @Column(name = "ingredientId", nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long ingredientId;

    @Column(name = "ingredientCategoryId", nullable = false)
    private Long ingredientCategoryId;

    @Column(name = "ingredientName", nullable = false, length = 64)
    private String ingredientName;

    @Column(name = "ingredientIconId")
    private Long ingredientIconId;

    @Column(name = "userId")
    private Long userId;

    @Builder
    public Ingredient(Long ingredientId, Long ingredientCategoryId, String ingredientName, Long ingredientIconId, Long userId) {

        Objects.requireNonNull(ingredientCategoryId, "재료 카테고리 아이디를 입력해주세요.");
        Preconditions.checkArgument(StringUtils.hasText(ingredientName), "재료명을 입력해주세요.");

        this.ingredientId = ingredientId;
        this.ingredientCategoryId = ingredientCategoryId;
        this.ingredientName = ingredientName;
        this.ingredientIconId = ingredientIconId;
        this.userId = userId;
    }

}