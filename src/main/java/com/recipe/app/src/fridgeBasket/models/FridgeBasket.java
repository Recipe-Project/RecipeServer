package com.recipe.app.src.fridgeBasket.models;

import com.recipe.app.common.entity.BaseEntity;
import com.recipe.app.src.ingredient.models.Ingredient;
import com.recipe.app.src.ingredientCategory.models.IngredientCategory;
import com.recipe.app.src.user.domain.User;
import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;

@NoArgsConstructor(access = AccessLevel.PUBLIC) // Unit Test 를 위해 PUBLIC
@EqualsAndHashCode(callSuper = false)
@Data // from lombok
@Entity // 필수, Class 를 Database Table화 해주는 것이다
@Table(name = "FridgeBasket") // Table 이름을 명시해주지 않으면 class 이름을 Table 이름으로 대체한다.
public class FridgeBasket extends BaseEntity {
    @Id // PK를 의미하는 어노테이션
    @Column(name = "idx", nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idx;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userIdx", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ingredientIdx")
    private Ingredient ingredient;

    @Column(name="ingredientName", nullable = false,length = 45)
    private String ingredientName;

    @Column(name="ingredientIcon")
    private String ingredientIcon;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ingredientCategoryIdx", nullable = false)
    private IngredientCategory ingredientCategory;

    @Column(name="count")
    private Integer count=1;

    @Column(name="storageMethod", nullable = false,length = 4)
    private String storageMethod="냉장";

    @Column(name = "expiredAt")
    private Date expiredAt;

    @Column(name="status", nullable=false, length=10)
    private String status="ACTIVE";

    public FridgeBasket(User user,Ingredient ingredient,String ingredientName,String ingredientIcon,IngredientCategory ingredientCategory){
        this.user = user;
        this.ingredient = ingredient;
        this.ingredientName = ingredientName;
        this.ingredientIcon = ingredientIcon;
        this.ingredientCategory = ingredientCategory;
    }
}