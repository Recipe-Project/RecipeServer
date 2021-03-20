package com.recipe.app.src.ingredient.models;

import com.recipe.app.config.BaseEntity;
import com.recipe.app.src.ingredientCategory.models.IngredientCategory;
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
@Table(name = "Ingredient") // Table 이름을 명시해주지 않으면 class 이름을 Table 이름으로 대체한다.
public class Ingredient extends BaseEntity {
    @Id // PK를 의미하는 어노테이션
    @Column(name = "ingredientIdx", nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer ingredientIdx;

    @Column(name="name", nullable = false,length = 45)
    private String name;

    @Column(name="icon")
    private String icon;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ingredientCategoryIdx", nullable = false)
    private IngredientCategory ingredientCategory;


    @Column(name="status", nullable=false, length=10)
    private String status="ACTIVE";

    public Ingredient(String name,  String icon, IngredientCategory ingredientCategory){
        this.name = name;
        this.icon = icon;
        this.ingredientCategory = ingredientCategory;
    }

}