package com.recipe.app.src.recipe.domain;

import com.recipe.app.common.entity.BaseEntity;
import com.recipe.app.src.ingredient.domain.Ingredient;
import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PUBLIC)
@EqualsAndHashCode(callSuper = false)
@Data
@Entity
@Table(name = "RecipeIngredient")
public class RecipeIngredient extends BaseEntity {
    @Id
    @Column(name = "idx", nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idx;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recipeId", nullable = false)
    private RecipeInfo recipeInfo;

    @Column(name = "irdntSn", nullable = false)
    private Integer irdntSn;

    @Column(name = "irdntNm", nullable = false, length = 10)
    private String irdntNm;

    @Column(name = "irdntCpcty", nullable = false, length = 10)
    private String irdntCpcty;

    @Column(name = "irdntTyCode", nullable = false)
    private Integer irdntTyCode;

    @Column(name = "irdntTyNm", nullable = false, length = 10)
    private String irdntTyNm;

    @Column(name = "status", nullable = false, length = 10)
    private String status = "ACTIVE";

    public RecipeIngredient(RecipeInfo recipeInfo, Integer irdntSn, String irdntNm, String irdntCpcty, Integer irdntTyCode, String irdntTyNm) {
        this.recipeInfo = recipeInfo;
        this.irdntSn = irdntSn;
        this.irdntNm = irdntNm;
        this.irdntCpcty = irdntCpcty;
        this.irdntTyCode = irdntTyCode;
        this.irdntTyNm = irdntTyNm;
    }

    public String getIcon(List<Ingredient> ingredients) {
        String iconNm = irdntNm;
        if (irdntNm.contains("대하")) {
            iconNm = "새우";
        } else if (irdntNm.contains("달걀")) {
            iconNm = "계란";
        } else if (irdntNm.contains("쇠고기")) {
            iconNm = "소고기";
        } else if (irdntNm.contains("후춧가루")) {
            iconNm = "후추";
        } else if (irdntNm.contains("다진마늘")) {
            iconNm = "간마늘";
        } else if (irdntNm.equals("어린잎채소") || irdntNm.equals("무순")) {
            iconNm = "새싹채소";
        } else if (irdntNm.equals("조갯살") || irdntNm.contains("바지락") || irdntNm.contains("전복") || irdntNm.contains("굴") || irdntNm.contains("가리비")) {
            iconNm = "조개";
        } else if (irdntNm.contains("케첩") || irdntNm.contains("케찹")) {
            iconNm = "케찹";
        } else if (irdntNm.contains("돼지")) {
            iconNm = "돼지고기";
        } else if (irdntNm.contains("닭") && !irdntNm.equals("닭발")) {
            iconNm = "닭고기";
        } else if (irdntNm.contains("연어") || irdntNm.contains("북어") || irdntNm.contains("대구") || irdntNm.contains("동태") || irdntNm.contains("광어") || irdntNm.contains("코다리") || irdntNm.contains("아귀") || irdntNm.contains("아구") || irdntNm.contains("조기")) {
            iconNm = "생선";
        } else if (irdntNm.equals("소면")) {
            iconNm = "국수";
        } else if (irdntNm.equals("김칫잎")) {
            iconNm = "김치";
        } else if (irdntNm.equals("인절미")) {
            iconNm = "떡";
        } else if (irdntNm.equals("고추가루")) {
            iconNm = "고춧가루";
        } else if (irdntNm.equals("올리브오일")) {
            iconNm = "올리브유";
        } else if (irdntNm.contains("양송이")) {
            iconNm = "버섯";
        } else if (irdntNm.contains("스파게티")) {
            iconNm = "파스타";
        } else if (irdntNm.contains("맛살")) {
            iconNm = "게";
        } else if (irdntNm.contains("포도씨유")) {
            iconNm = "식용유";
        }
        if (irdntNm.contains("스톡") || irdntNm.contains("국물") || irdntNm.contains("다시물") || irdntNm.contains("육수") || irdntNm.equals("우무") || irdntNm.contains("알") || irdntNm.contains("고추냉이") || irdntNm.equals("마늘종")) {
            iconNm = "";
        }
        String keyword = iconNm;
        return ingredients.stream()
                .filter((i) -> i.getName().equals(keyword))
                .map(Ingredient::getIcon)
                .findFirst()
                .orElse(null);
    }
}
