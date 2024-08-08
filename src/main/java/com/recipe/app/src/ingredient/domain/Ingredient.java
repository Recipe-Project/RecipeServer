package com.recipe.app.src.ingredient.domain;

import com.google.common.base.Preconditions;
import com.recipe.app.src.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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
@Table(name = "Ingredient")
public class Ingredient extends BaseEntity {
    @Id
    @Column(name = "ingredientId", nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long ingredientId;

    @Column(name = "ingredientCategoryId", nullable = false)
    private Long ingredientCategoryId;

    @Column(name = "ingredientName", nullable = false, length = 64)
    private String ingredientName;

    @Column(name = "ingredientIconId")
    private Long ingredientIconId;

    @Column(name = "userId")
    private Long userId;

    @Builder
    public Ingredient(Long ingredientId, Long ingredientCategoryId, String ingredientName, Long ingredientIconId, Long userId) {

        Objects.requireNonNull(ingredientCategoryId, "재료 카테고리 아이디를 입력해주세요.");
        Preconditions.checkArgument(StringUtils.hasText(ingredientName), "재료명을 입력해주세요.");

        this.ingredientId = ingredientId;
        this.ingredientCategoryId = ingredientCategoryId;
        this.ingredientName = ingredientName;
        this.ingredientIconId = ingredientIconId;
        this.userId = userId;
    }

    public List<String> getSimilarIngredientName() {
        if (ingredientName.equals("새우"))
            return List.of("대하");
        if (ingredientName.equals("대하"))
            return List.of("새우");
        if (ingredientName.equals("계란"))
            return List.of("달걀");
        if (ingredientName.equals("달걀"))
            return List.of("계란");
        if (ingredientName.equals("소고기"))
            return List.of("쇠고기");
        if (ingredientName.equals("쇠고기"))
            return List.of("소고기");
        if (ingredientName.equals("후추"))
            return List.of("후춧가루");
        if (ingredientName.equals("후춧가루"))
            return List.of("후추");
        if (ingredientName.equals("간마늘"))
            return List.of("다진마늘");
        if (ingredientName.equals("다진마늘"))
            return List.of("간마늘");
        if (ingredientName.equals("새싹채소"))
            return List.of("어린잎채소", "무순");
        if (ingredientName.equals("어린잎채소"))
            return List.of("새싹채소");
        if (ingredientName.equals("무순"))
            return List.of("새싹채소");
        if (ingredientName.equals("조개"))
            return List.of("조갯살", "바지락");
        if (ingredientName.equals("조갯살"))
            return List.of("조개");
        if (ingredientName.equals("바지락"))
            return List.of("조개");
        if (ingredientName.equals("케찹"))
            return List.of("케첩");
        if (ingredientName.equals("케첩"))
            return List.of("케찹");
        if (ingredientName.equals("소면"))
            return List.of("국수");
        if (ingredientName.equals("국수"))
            return List.of("소면");
        if (ingredientName.equals("김치"))
            return List.of("김칫잎");
        if (ingredientName.equals("김칫잎"))
            return List.of("김치");
        if (ingredientName.equals("고춧가루"))
            return List.of("고추가루");
        if (ingredientName.equals("고추가루"))
            return List.of("고춧가루");
        if (ingredientName.equals("올리브유"))
            return List.of("올리브오일");
        if (ingredientName.equals("올리브오일"))
            return List.of("올리브유");
        if (ingredientName.equals("파스타"))
            return List.of("스파게티");
        if (ingredientName.equals("스파게티"))
            return List.of("파스타");
        if (ingredientName.equals("포도씨유"))
            return List.of("식용유");
        if (ingredientName.equals("식용유"))
            return List.of("포도씨유");
        return new ArrayList<>();
    }
}