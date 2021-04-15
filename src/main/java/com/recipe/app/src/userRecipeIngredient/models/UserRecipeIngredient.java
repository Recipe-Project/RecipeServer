package com.recipe.app.src.userRecipeIngredient.models;


import com.recipe.app.config.BaseEntity;
import com.recipe.app.src.ingredient.models.Ingredient;
import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;


@NoArgsConstructor(access = AccessLevel.PUBLIC) // Unit Test 를 위해 PUBLIC
@EqualsAndHashCode(callSuper = false)
@Data // from lombok
@Entity // 필수, Class 를 Database Table화 해주는 것이다
@Table(name = "UserRecipeIngredient") // Table 이름을 명시해주지 않으면 class 이름을 Table 이름으로 대체한다.
public class UserRecipeIngredient extends BaseEntity {
    @Id // PK를 의미하는 어노테이션
    @Column(name = "idx", nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idx;

    @Column(name="userRecipeIdx", nullable = false)
    private Integer userRecipeIdx;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ingredientIdx")
    private Ingredient ingredient;


    @Column(name = "ingredientIcon" , nullable=false)
    private String ingredientIcon;

    @Column(name = "ingredientName" , nullable=false, length = 10)
    private String ingredientName;

    @Column(name="status", nullable=false, length=10)
    private String status="ACTIVE";

    public UserRecipeIngredient(Integer userRecipeIdx, Ingredient ingredient,String ingredientIcon,String ingredientName){
        this.userRecipeIdx = userRecipeIdx;
        this.ingredient = ingredient;
        this.ingredientIcon = ingredientIcon;
        this.ingredientName = ingredientName;
    }
}
