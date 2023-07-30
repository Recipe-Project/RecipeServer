package com.recipe.app.src.ingredient.infra;

import com.recipe.app.common.entity.BaseEntity;
import com.recipe.app.src.ingredient.domain.IngredientCategory;
import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PUBLIC)
@EqualsAndHashCode(callSuper = false)
@Data
@Entity
@Table(name = "IngredientCategoryEntity")
public class IngredientCategoryEntity extends BaseEntity {
    @Id
    @Column(name = "ingredientCategoryId", nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long ingredientCategoryId;

    @Column(name = "ingredientCategoryName", nullable = false, length = 10)
    private String ingredientCategoryName;

    @OneToMany(mappedBy = "ingredientCategoryEntity", cascade = CascadeType.ALL)
    private List<IngredientEntity> ingredientEntities = new ArrayList<>();

    public IngredientCategory toModel() {
        return IngredientCategory.builder()
                .ingredientCategoryId(ingredientCategoryId)
                .ingredientCategoryName(ingredientCategoryName)
                .createdAt(getCreatedAt())
                .updatedAt(getUpdatedAt())
                .build();
    }
}