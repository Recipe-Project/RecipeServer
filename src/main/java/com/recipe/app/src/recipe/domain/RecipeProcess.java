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
@Table(name = "RecipeProcess")
public class RecipeProcess extends BaseEntity {

    @Id
    @Column(name = "recipeProcessId", nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long recipeProcessId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recipeId")
    private Recipe recipe;

    @Column(name = "cookingNo", nullable = false)
    private Integer cookingNo;

    @Column(name = "cookingDescription", nullable = false)
    private String cookingDescription;

    @Column(name = "recipeProcessImgUrl")
    private String recipeProcessImgUrl;

    @Builder
    public RecipeProcess(Long recipeProcessId, Recipe recipe, Integer cookingNo, String cookingDescription, String recipeProcessImgUrl) {

        Objects.requireNonNull(cookingNo, "레시피 요리 순서를 입력해주세요.");
        Preconditions.checkArgument(StringUtils.hasText(cookingDescription), "레시피 요리 과정 설명을 입력해주세요.");

        this.recipeProcessId = recipeProcessId;
        this.recipe = recipe;
        recipe.processes.add(this);
        this.cookingNo = cookingNo;
        this.cookingDescription = cookingDescription;
        this.recipeProcessImgUrl = recipeProcessImgUrl;
    }

    public void delete() {
        this.recipe = null;
    }
}
