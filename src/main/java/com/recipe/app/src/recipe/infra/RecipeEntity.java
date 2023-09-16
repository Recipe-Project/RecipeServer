package com.recipe.app.src.recipe.infra;

import com.recipe.app.common.entity.BaseEntity;
import com.recipe.app.src.recipe.domain.Recipe;
import com.recipe.app.src.user.infra.UserEntity;
import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PUBLIC)
@EqualsAndHashCode(callSuper = false)
@Data
@Entity
@Table(name = "Recipe")
public class RecipeEntity extends BaseEntity {

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

    @Column(name = "levelNm", length = 2)
    private String levelNm;

    @Column(name = "imgUrl")
    private String imgUrl;

    @Column(name = "quantity")
    private Long quantity;

    @Column(name = "calorie")
    private Long calorie;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId")
    private UserEntity user;

    @Column(name = "hiddenYn", nullable = false)
    private String hiddenYn = "Y";

    @OneToMany(mappedBy = "recipe")
    private List<RecipeScrapEntity> recipeScraps = new ArrayList<>();

    @OneToMany(mappedBy = "recipe")
    private List<RecipeViewEntity> recipeViews = new ArrayList<>();

    public Recipe toModel() {
        return Recipe.builder()
                .recipeId(recipeId)
                .recipeNm(recipeNm)
                .introduction(introduction)
                .cookingTime(cookingTime)
                .levelNm(levelNm)
                .imgUrl(imgUrl)
                .quantity(quantity)
                .calorie(calorie)
                .user(user != null ? user.toModel() : null)
                .isHidden(hiddenYn.equals("Y"))
                .recipeIngredients(recipeIngredients.stream()
                        .map(RecipeIngredientEntity::toModel)
                        .collect(Collectors.toList()))
                .recipeProcesses(recipeProcesses.stream()
                        .map(RecipeProcessEntity::toModel)
                        .collect(Collectors.toList()))
                .scrapUsers(recipeScraps.stream()
                        .map(s -> s.getUser().toModel())
                        .collect(Collectors.toList()))
                .viewUsers(recipeViews.stream()
                        .map(v -> v.getUser().toModel())
                        .collect(Collectors.toList()))
                .build();
    }

    public static RecipeEntity fromModel(Recipe recipe) {
        RecipeEntity recipeEntity = new RecipeEntity();
        recipeEntity.recipeId = recipe.getRecipeId();
        recipeEntity.recipeNm = recipe.getRecipeNm();
        recipeEntity.introduction = recipe.getIntroduction();
        recipeEntity.cookingTime = recipe.getCookingTime();
        recipeEntity.levelNm = recipe.getLevelNm();
        recipeEntity.imgUrl = recipe.getImgUrl();
        recipeEntity.quantity = recipe.getQuantity();
        recipeEntity.calorie = recipe.getCalorie();
        recipeEntity.user = UserEntity.fromModel(recipe.getUser());
        recipeEntity.hiddenYn = recipe.isHidden() ? "Y" : "N";
        return recipeEntity;
    }
}
