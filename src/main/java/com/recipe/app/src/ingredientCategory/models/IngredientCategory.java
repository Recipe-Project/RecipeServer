package com.recipe.app.src.ingredientCategory.models;

import com.recipe.app.common.entity.BaseEntity;
import com.recipe.app.src.fridge.domain.Fridge;
import com.recipe.app.src.fridgeBasket.domain.FridgeBasket;
import com.recipe.app.src.ingredient.models.Ingredient;
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
@Table(name = "IngredientCategory") // Table 이름을 명시해주지 않으면 class 이름을 Table 이름으로 대체한다.
public class IngredientCategory extends BaseEntity {
    @Id // PK를 의미하는 어노테이션
    @Column(name = "ingredientCategoryIdx", nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer ingredientCategoryIdx; // Integer -> Long 21억 지나면 터지니까 Long

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