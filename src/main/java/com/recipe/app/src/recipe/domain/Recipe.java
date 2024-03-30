package com.recipe.app.src.recipe.domain;

import com.google.common.base.Preconditions;
import com.recipe.app.common.entity.BaseEntity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.util.StringUtils;

import javax.persistence.*;
import java.util.Objects;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "Recipe")
public class Recipe extends BaseEntity {

    @Id
    @Column(name = "recipeId", nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long recipeId;

    @Column(name = "recipeNm", nullable = false, length = 45)
    private String recipeNm;

    @Column(name = "introduction", length = 200)
    private String introduction;

    @Column(name = "cookingTime")
    private Long cookingTime;

    @Column(name = "level", length = 2)
    private RecipeLevel level;

    @Column(name = "imgUrl")
    private String imgUrl;

    @Column(name = "quantity")
    private Long quantity;

    @Column(name = "calorie")
    private Long calorie;

    @Column(name = "userId", nullable = false)
    private Long userId;

    @Column(name = "hiddenYn", nullable = false)
    private String hiddenYn = "Y";

    @Builder
    public Recipe(Long recipeId, String recipeNm, String introduction, Long cookingTime, RecipeLevel level,
                  String imgUrl, Long quantity, Long calorie, Long userId, boolean isHidden) {

        Preconditions.checkArgument(StringUtils.hasText(recipeNm), "레시피명을 입력해주세요.");
        Objects.requireNonNull(userId, "유저 아이디를 입력해주세요.");

        this.recipeId = recipeId;
        this.recipeNm = recipeNm;
        this.introduction = introduction;
        this.cookingTime = cookingTime;
        this.level = level;
        this.imgUrl = imgUrl;
        this.quantity = quantity;
        this.calorie = calorie;
        this.userId = userId;
        this.hiddenYn = isHidden ? "Y" : "N";
    }

    public Recipe(RecipeWithRate recipeWithRate) {
        this.recipeId = recipeWithRate.getRecipeId();
        this.recipeNm = recipeWithRate.getRecipeNm();
        this.introduction = recipeWithRate.getIntroduction();
        this.cookingTime = recipeWithRate.getCookingTime();
        this.level = recipeWithRate.getLevel();
        this.imgUrl = recipeWithRate.getImgUrl();
        this.quantity = recipeWithRate.getQuantity();
        this.calorie = recipeWithRate.getCalorie();
        this.userId = recipeWithRate.getUserId();
        this.hiddenYn = recipeWithRate.getHiddenYn();
        this.setCreatedAt(recipeWithRate.getCreatedAt());
        this.setUpdatedAt(recipeWithRate.getUpdatedAt());
    }

    public void updateRecipe(String recipeNm, String imgUrl) {
        this.recipeNm = recipeNm;
        this.imgUrl = imgUrl;
    }

    public boolean isHidden() {
        return hiddenYn.equals("Y");
    }
}
