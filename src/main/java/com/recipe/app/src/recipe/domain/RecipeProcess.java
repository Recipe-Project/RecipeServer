package com.recipe.app.src.recipe.domain;

import com.recipe.app.common.entity.BaseEntity;
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
public class RecipeProcess extends BaseEntity {
    @Id
    @Column(name = "idx", nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idx;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recipeId", nullable = false)
    private RecipeInfo recipeInfo;

    @Column(name = "cookingNo", nullable = false)
    private Integer cookingNo;

    @Column(name = "cookingDc", nullable = false, length = 500)
    private String cookingDc;

    @Column(name = "streStepImageUrl", nullable = false)
    private String streStepImageUrl;

    @Column(name = "stepTip", nullable = false, length = 500)
    private String stepTip;

    @Column(name = "status", nullable = false, length = 10)
    private String status = "ACTIVE";

    public RecipeProcess(RecipeInfo recipeInfo, Integer cookingNo, String cookingDc, String streStepImageUrl, String stepTip) {
        this.recipeInfo = recipeInfo;
        this.cookingNo = cookingNo;
        this.cookingDc = cookingDc;
        this.streStepImageUrl = streStepImageUrl;
        this.stepTip = stepTip;
    }

}
