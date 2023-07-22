package com.recipe.app.src.ingredient.domain;

import com.recipe.app.common.entity.BaseEntity;
import com.recipe.app.src.fridge.domain.Fridge;
import com.recipe.app.src.fridgeBasket.domain.FridgeBasket;
import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PUBLIC)
@EqualsAndHashCode(callSuper = false)
@Data
@Entity
@Table(name = "IngredientCategory")
public class IngredientCategory extends BaseEntity {
    @Id
    @Column(name = "ingredientCategoryIdx", nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer ingredientCategoryIdx;

    @Column(name = "name", nullable = false, length = 45)
    private String name;

    @OneToMany(mappedBy = "ingredientCategory", cascade = CascadeType.ALL)
    private List<Ingredient> ingredients = new ArrayList<>();

    @OneToMany(mappedBy = "ingredientCategory", cascade = CascadeType.ALL)
    private List<FridgeBasket> fridgeBasket = new ArrayList<>();

    @OneToMany(mappedBy = "ingredientCategory", cascade = CascadeType.ALL)
    private List<Fridge> fridge = new ArrayList<>();

    @Column(name = "status", nullable = false, length = 10)
    private String status = "ACTIVE";

    public IngredientCategory(String name) {
        this.name = name;
    }

}