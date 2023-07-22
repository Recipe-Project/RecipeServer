package com.recipe.app.src.ingredient.domain;

import com.recipe.app.common.entity.BaseEntity;
import com.recipe.app.src.fridgeBasket.domain.FridgeBasket;
import com.recipe.app.src.userRecipe.domain.UserRecipeIngredient;
import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;


@NoArgsConstructor(access = AccessLevel.PUBLIC)
@EqualsAndHashCode(callSuper = false)
@Data
@Entity
@Table(name = "Ingredient")
public class Ingredient extends BaseEntity implements Comparable<Ingredient> {
    @Id
    @Column(name = "ingredientIdx", nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer ingredientIdx;

    @Column(name = "name", nullable = false, length = 45)
    private String name;

    @Column(name = "icon", nullable = false)
    private String icon;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ingredientCategoryIdx", nullable = false)
    private IngredientCategory ingredientCategory;


    @Column(name = "status", nullable = false, length = 10)
    private String status = "ACTIVE";


    @OneToMany(mappedBy = "ingredient", cascade = CascadeType.ALL)
    private List<FridgeBasket> fridgeBasket = new ArrayList<>();

    @OneToMany(mappedBy = "ingredient", cascade = CascadeType.ALL)
    private List<UserRecipeIngredient> userRecipeIngredient = new ArrayList<>();

    public Ingredient(String name, String icon, IngredientCategory ingredientCategory) {
        this.name = name;
        this.icon = icon;
        this.ingredientCategory = ingredientCategory;
    }

    @Override
    public int compareTo(@NotNull Ingredient ingredient) {
        if (this.getName().length() < ingredient.getName().length()) {
            return -1;
        } else if (this.getName().length() > ingredient.getName().length()) {
            return 1;
        } else {
            return 0;
        }
    }
}