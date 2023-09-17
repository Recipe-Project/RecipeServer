package com.recipe.app.src.recipe.infra;

import com.recipe.app.common.entity.BaseEntity;
import com.recipe.app.src.recipe.domain.RecipeProcess;
import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@NoArgsConstructor(access = AccessLevel.PUBLIC)
@EqualsAndHashCode(callSuper = false)
@Data
@Entity
@Table(name = "RecipeProcess")
public class RecipeProcessEntity extends BaseEntity {

    @Id
    @Column(name = "recipeProcessId", nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long recipeProcessId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recipeId", nullable = false)
    private RecipeEntity recipe;

    @Column(name = "cookingNo", nullable = false)
    private Integer cookingNo;

    @Column(name = "cookingDescription", nullable = false)
    private String cookingDescription;

    @Column(name = "recipeProcessImgUrl")
    private String recipeProcessImgUrl;

    public static RecipeProcessEntity fromModel(RecipeProcess recipeProcess) {
        RecipeProcessEntity recipeProcessEntity = new RecipeProcessEntity();
        recipeProcessEntity.recipeProcessId = recipeProcess.getRecipeProcessId();
        recipeProcessEntity.recipe = RecipeEntity.fromModel(recipeProcess.getRecipe());
        recipeProcessEntity.cookingNo = recipeProcess.getCookingNo();
        recipeProcessEntity.cookingDescription = recipeProcess.getCookingDescription();
        recipeProcessEntity.recipeProcessImgUrl = recipeProcess.getRecipeProcessImgUrl();
        return recipeProcessEntity;
    }

    public RecipeProcess toModel() {
        return RecipeProcess.builder()
                .recipeProcessId(recipeProcessId)
                .recipe(recipe.toModel())
                .cookingNo(cookingNo)
                .cookingDescription(cookingDescription)
                .recipeProcessImgUrl(recipeProcessImgUrl)
                .createdAt(getCreatedAt())
                .updatedAt(getUpdatedAt())
                .build();
    }
}
