package com.recipe.app.src.recipeProcess.models;

import com.recipe.app.common.entity.BaseEntity;
import com.recipe.app.src.recipeInfo.models.RecipeInfo;
import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;


@NoArgsConstructor(access = AccessLevel.PUBLIC) // Unit Test 를 위해 PUBLIC
@EqualsAndHashCode(callSuper = false)
@Data // from lombok
@Entity // 필수, Class 를 Database Table화 해주는 것이다
@Table(name = "RecipeProcess") // Table 이름을 명시해주지 않으면 class 이름을 Table 이름으로 대체한다.
public class RecipeProcess extends BaseEntity {
    @Id // PK를 의미하는 어노테이션
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

    @Column(name="status", nullable=false, length=10)
    private String status="ACTIVE";

    public RecipeProcess(RecipeInfo recipeInfo, Integer cookingNo, String cookingDc, String streStepImageUrl, String stepTip){
        this.recipeInfo = recipeInfo;
        this.cookingNo = cookingNo;
        this.cookingDc = cookingDc;
        this.streStepImageUrl = streStepImageUrl;
        this.stepTip = stepTip;
    }

}
