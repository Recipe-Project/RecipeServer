package com.recipe.app.src.recipe.domain;

import com.google.common.base.Preconditions;
import com.recipe.app.src.common.entity.BaseEntity;
import com.recipe.app.src.recipe.infra.RecipeLevelPersistConverter;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
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

    @Convert(converter = RecipeLevelPersistConverter.class)
    @Column(name = "level", length = 2)
    private RecipeLevel level;

    @Column(name = "imgUrl")
    private String imgUrl;

    @Column(name = "quantity")
    private Long quantity;

    @Column(name = "calorie")
    private Long calorie;

    @Column(name = "userId")
    private Long userId;

    @Column(name = "hiddenYn", nullable = false)
    private String hiddenYn = "Y";

    @Column(name = "scrapCnt", nullable = false)
    private long scrapCnt;

    @Column(name = "viewCnt", nullable = false)
    private long viewCnt;

    @Column(name = "reportYn", nullable = false)
    private String reportYn = "N";

    @OneToMany(mappedBy = "recipe", cascade = CascadeType.ALL)
    List<RecipeIngredient> ingredients = new ArrayList<>();

    @OneToMany(mappedBy = "recipe", cascade = CascadeType.ALL)
    List<RecipeProcess> processes = new ArrayList<>();

    @Builder
    public Recipe(Long recipeId, String recipeNm, String introduction, Long cookingTime, RecipeLevel level,
                  String imgUrl, Long quantity, Long calorie, Long userId, boolean isHidden, long scrapCnt, long viewCnt) {

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
        this.scrapCnt = scrapCnt;
        this.viewCnt = viewCnt;
    }

    public void updateRecipe(Recipe updateRecipe) {

        this.recipeNm = updateRecipe.recipeNm;
        this.introduction = updateRecipe.introduction;
        this.cookingTime = updateRecipe.cookingTime;
        this.level = updateRecipe.level;
        this.imgUrl = updateRecipe.imgUrl;
        this.hiddenYn = updateRecipe.hiddenYn;

        ingredients.forEach(RecipeIngredient::delete);
        this.ingredients = updateRecipe.ingredients;

        processes.forEach(RecipeProcess::delete);
        this.processes = updateRecipe.processes;
    }

    public boolean isHidden() {
        return hiddenYn.equals("Y");
    }

    public boolean isReported() {
        return reportYn.equals("Y");
    }

    public void plusScrapCnt() {
        this.scrapCnt++;
    }

    public void minusScrapCnt() {
        this.scrapCnt--;
    }

    public void plusViewCnt() {
        this.viewCnt++;
    }

    public void report() {
        this.reportYn = "Y";
        this.hiddenYn = "Y";
    }

    public long calculateIngredientMatchRate(List<String> ingredientNamesInFridge) {

        long ingredientMatchCnt = ingredients.stream()
                .filter(ingredient -> ingredient.hasInFridge(ingredientNamesInFridge))
                .count();

        return ingredientMatchCnt / ingredients.size() * 100;
    }
}
