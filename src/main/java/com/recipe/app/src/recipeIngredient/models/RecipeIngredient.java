package com.recipe.app.src.recipeIngredient.models;

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
@Table(name = "RecipeIngredient") // Table 이름을 명시해주지 않으면 class 이름을 Table 이름으로 대체한다.
public class RecipeIngredient extends BaseEntity {
    @Id // PK를 의미하는 어노테이션
    @Column(name = "idx", nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idx;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recipeId", nullable = false)
    private RecipeInfo recipeInfo;

    @Column(name = "irdntSn", nullable = false)
    private Integer irdntSn;

    @Column(name = "irdntNm", nullable = false,length = 10)
    private String irdntNm;

    @Column(name = "irdntCpcty", nullable = false,length = 10)
    private String irdntCpcty;

    @Column(name = "irdntTyCode", nullable = false)
    private Integer irdntTyCode;

    @Column(name="irdntTyNm", nullable = false, length = 10)
    private String irdntTyNm;

    @Column(name="status", nullable=false, length=10)
    private String status="ACTIVE";


    public RecipeIngredient(RecipeInfo recipeInfo,Integer irdntSn, String irdntNm,String irdntCpcty,Integer irdntTyCode,String irdntTyNm){
        this.recipeInfo = recipeInfo;
        this.irdntSn = irdntSn;
        this.irdntNm = irdntNm;
        this.irdntCpcty = irdntCpcty;
        this.irdntTyCode = irdntTyCode;
        this.irdntTyNm = irdntTyNm;

    }

}
