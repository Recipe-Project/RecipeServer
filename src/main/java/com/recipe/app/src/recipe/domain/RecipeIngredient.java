package com.recipe.app.src.recipe.domain;

import com.google.common.base.Preconditions;
import com.recipe.app.src.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.util.StringUtils;

import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "RecipeIngredient")
public class RecipeIngredient extends BaseEntity {

    @Id
    @Column(name = "recipeIngredientId", nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long recipeIngredientId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recipeId")
    private Recipe recipe;

    @Column(name = "ingredientName", nullable = false, length = 64)
    private String ingredientName;

    @Column(name = "searchTokens", length = 128)
    private String searchTokens;

    @Column(name = "ingredientIconId")
    private Long ingredientIconId;

    @Column(name = "quantity")
    private String quantity;

    @Column(name = "unit")
    private String unit;

    @Builder
    public RecipeIngredient(Long recipeIngredientId, Recipe recipe, String ingredientName, Long ingredientIconId, String quantity, String unit) {

        Preconditions.checkArgument(StringUtils.hasText(ingredientName), "레시피 재료명을 입력해주세요.");

        this.recipeIngredientId = recipeIngredientId;
        this.recipe = recipe;
        if (recipe != null) {
            recipe.ingredients.add(this);
        }
        this.ingredientName = ingredientName;
        this.ingredientIconId = ingredientIconId;
        this.quantity = quantity;
        this.unit = unit;
    }

    void setRecipe(Recipe recipe) {
        this.recipe = recipe;
    }

    public boolean hasInFridge(List<String> ingredientNames) {
        return ingredientNames.contains(ingredientName);
    }

    @PrePersist
    @PreUpdate
    private void refreshSearchTokens() {
        // 재료명은 짧고 단일 명사가 대부분이라 nori stopword 정책이 오히려
        // 도메인 단어("갓", "다시다" 등)를 제거해버린다. 단순 정규화로 대체.
        this.searchTokens = ingredientName == null ? "" : ingredientName.toLowerCase().trim();
    }
}
