package com.recipe.app.src.fridge.models;

import com.recipe.app.config.BaseEntity;
import com.recipe.app.src.ingredient.models.Ingredient;
import com.recipe.app.src.ingredientCategory.models.IngredientCategory;
import com.recipe.app.src.user.models.User;
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
@Table(name = "Fridge") // Table 이름을 명시해주지 않으면 class 이름을 Table 이름으로 대체한다.
public class Fridge extends BaseEntity {
    @Id // PK를 의미하는 어노테이션
    @Column(name = "idx", nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idx;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userIdx", nullable = false)
    private User user;

    @Column(name = "ingredientName", nullable = false, length = 20)
    private String ingredientName;

    @Column(name = "ingredientIcon") //null 임시허용
    private String ingredientIcon;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ingredientCategoryIdx", nullable = false)
    private IngredientCategory ingredientCategory;

    @Column(name = "storageMethod", nullable = false, length = 2)
    private String storageMethod;

    @Column(name = "expiredAt", nullable = false)
    private Date expiredAt;

    @Column(name = "count", nullable = false)
    private Integer count;

    @Column(name = "status", nullable = false, length = 10)
    private String status = "ACTIVE";

    public Fridge(User user, String ingredientName, String ingredientIcon, IngredientCategory ingredientCategory, String storageMethod, Date expiredAt, Integer count) {
        this.user = user;
        this.ingredientName = ingredientName;
        this.ingredientIcon = ingredientIcon;
        this.ingredientCategory = ingredientCategory;
        this.storageMethod = storageMethod;
        this.expiredAt = expiredAt;
        this.count = count;
    }

}