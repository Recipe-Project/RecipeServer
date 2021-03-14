package com.recipe.app.src.recipeInfo.models;

import com.recipe.app.config.BaseEntity;
import com.recipe.app.src.scrapPublic.models.ScrapPublic;
import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;


@NoArgsConstructor(access = AccessLevel.PUBLIC) // Unit Test 를 위해 PUBLIC
@EqualsAndHashCode(callSuper = false)
@Data // from lombok
@Entity // 필수, Class 를 Database Table화 해주는 것이다
@Table(name = "RecipeInfo") // Table 이름을 명시해주지 않으면 class 이름을 Table 이름으로 대체한다.
public class RecipeInfo extends BaseEntity {
    @Id // PK를 의미하는 어노테이션
    @Column(name = "recipeId", nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer recipeId;

    @Column(name="recipeNmKo", nullable = false,length = 45)
    private String recipeNmKo;

    @Column(name = "sumry", nullable = false,length = 200)
    private String sumry;

    @Column(name = "nationCode", nullable = false)
    private Integer nationCode;

    @Column(name = "nationNm", nullable = false,length = 20)
    private String nationNm;

    @Column(name = "tyCode", nullable = false)
    private Integer tyCode;

    @Column(name="tyNm", nullable = false, length = 20)
    private String tyNm;

    @Column(name = "cookingTime", nullable = false, length = 20)
    private String cookingTime;

    @Column(name = "calorie", nullable = false, length = 20)
    private String calorie;

    @Column(name = "qnt", nullable = false, length = 20)
    private String qnt;

    @Column(name = "levelNm", nullable = false, length = 20)
    private String levelNm;

    @Column(name = "irdntCode", nullable = false, length = 20)
    private String irdntCode;

    @Column(name = "pcNm", nullable = false, length = 20)
    private String pcNm;

    @Column(name = "imgUrl", nullable = false)
    private String imgUrl;

    @Column(name = "detUrl", nullable = false)
    private String detUrl;

    @Column(name="status", nullable=false, length=10)
    private String status="ACTIVE";

    @OneToMany(mappedBy = "recipeInfo", cascade = CascadeType.ALL)
    private List<ScrapPublic> scrapPublics = new ArrayList<>();

    public RecipeInfo(String recipeNmKo,String sumry,Integer nationCode, String nationNm,Integer tyCode,String tyNm,String cookingTime,String calorie,String qnt, String levelNm,String irdntCode ,
                      String pcNm, String imgUrl, String detUrl){
        this.recipeNmKo = recipeNmKo;
        this.sumry = sumry;
        this.nationCode = nationCode;
        this.nationNm = nationNm;
        this.tyCode = tyCode;
        this.tyNm = tyNm;
        this.cookingTime = cookingTime;
        this.calorie = calorie;
        this.qnt = qnt;
        this.irdntCode = irdntCode;
        this.pcNm = pcNm;
        this.imgUrl = imgUrl;
        this.detUrl = detUrl;
    }

}
