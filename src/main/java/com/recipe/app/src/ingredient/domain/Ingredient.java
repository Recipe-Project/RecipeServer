package com.recipe.app.src.ingredient.domain;

import com.google.common.base.Preconditions;
import com.recipe.app.common.entity.BaseEntity;
import com.recipe.app.src.user.domain.User;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.util.StringUtils;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "Ingredient")
public class Ingredient extends BaseEntity implements Comparable<Ingredient> {
    @Id
    @Column(name = "ingredientId", nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long ingredientId;

    @Column(name = "ingredientCategoryId", nullable = false)
    private Long ingredientCategoryId;

    @Column(name = "ingredientName", nullable = false, length = 64)
    private String ingredientName;

    @Column(name = "ingredientIconUrl")
    private String ingredientIconUrl;

    @Column(name = "userId")
    private Long userId;

    @Column(name = "defaultYn", length = 1)
    private String defaultYn;

    @Column(name = "hiddenYn", length = 1)
    private String hiddenYn;

    @Builder
    public Ingredient(Long ingredientId, Long ingredientCategoryId, String ingredientName, String ingredientIconUrl, Long userId, boolean isDefault, boolean isHidden) {

        Objects.requireNonNull(ingredientCategoryId, "재료 카테고리 아이디를 입력해주세요.");
        Preconditions.checkArgument(StringUtils.hasText(ingredientName), "재료명을 입력해주세요.");

        this.ingredientId = ingredientId;
        this.ingredientCategoryId = ingredientCategoryId;
        this.ingredientName = ingredientName;
        this.ingredientIconUrl = ingredientIconUrl;
        this.userId = userId;
        this.defaultYn = isDefault ? "Y" : "N";
        this.hiddenYn = isHidden ? "Y" : "N";
    }

    @Override
    public int compareTo(@NotNull Ingredient ingredient) {
        return Integer.compare(this.getIngredientName().length(), ingredient.getIngredientName().length());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        com.recipe.app.src.ingredient.domain.Ingredient ingredient = (com.recipe.app.src.ingredient.domain.Ingredient) o;

        if (ingredientId.equals(ingredient.getIngredientId()))
            return true;

        if ((ingredientName.equals("새우") && ingredient.getIngredientName().equals("대하"))
                || (ingredientName.equals("대하") && ingredient.getIngredientName().equals("새우"))
                || (ingredientName.equals("계란") && ingredient.getIngredientName().equals("달걀"))
                || (ingredientName.equals("달걀") && ingredient.getIngredientName().equals("계란"))
                || (ingredientName.equals("소고기") && ingredient.getIngredientName().equals("쇠고기"))
                || (ingredientName.equals("쇠고기") && ingredient.getIngredientName().equals("소고기"))
                || (ingredientName.equals("후추") && ingredient.getIngredientName().equals("후춧가루"))
                || (ingredientName.equals("후춧가루") && ingredient.getIngredientName().equals("후추"))
                || (ingredientName.equals("간마늘") && ingredient.getIngredientName().equals("다진마늘"))
                || (ingredientName.equals("다진마늘") && ingredient.getIngredientName().equals("간마늘"))
                || (ingredientName.equals("새싹채소") && ingredient.getIngredientName().equals("어린잎채소"))
                || (ingredientName.equals("어린잎채소") && ingredient.getIngredientName().equals("새싹채소"))
                || (ingredientName.equals("새싹채소") && ingredient.getIngredientName().equals("무순"))
                || (ingredientName.equals("무순") && ingredient.getIngredientName().equals("새싹채소"))
                || (ingredientName.contains("조개") && ingredient.getIngredientName().contains("조개"))
                || (ingredientName.equals("조개") && ingredient.getIngredientName().equals("조갯살"))
                || (ingredientName.equals("조갯살") && ingredient.getIngredientName().equals("조개"))
                || (ingredientName.equals("조개") && ingredient.getIngredientName().equals("바지락"))
                || (ingredientName.equals("바지락") && ingredient.getIngredientName().equals("조개"))
                || (ingredientName.equals("케찹") && ingredient.getIngredientName().equals("케첩"))
                || (ingredientName.equals("케첩") && ingredient.getIngredientName().equals("케찹"))
                || (ingredientName.equals("소면") && ingredient.getIngredientName().equals("국수"))
                || (ingredientName.equals("국수") && ingredient.getIngredientName().equals("소면"))
                || (ingredientName.equals("김치") && ingredient.getIngredientName().equals("김칫잎"))
                || (ingredientName.equals("김칫잎") && ingredient.getIngredientName().equals("김치"))
                || (ingredientName.equals("고춧가루") && ingredient.getIngredientName().equals("고추가루"))
                || (ingredientName.equals("고추가루") && ingredient.getIngredientName().equals("고춧가루"))
                || (ingredientName.equals("올리브유") && ingredient.getIngredientName().equals("올리브오일"))
                || (ingredientName.equals("올리브오일") && ingredient.getIngredientName().equals("올리브유"))
                || (ingredientName.equals("파스타") && ingredient.getIngredientName().equals("스파게티"))
                || (ingredientName.equals("스파게티") && ingredient.getIngredientName().equals("파스타"))
                || (ingredientName.equals("포도씨유") && ingredient.getIngredientName().equals("식용유"))
                || (ingredientName.equals("식용유") && ingredient.getIngredientName().equals("포도씨유"))) {
            return true;
        }

        return ingredientName.replace(" ", "").equals(ingredient.getIngredientName().replace(" ", ""));
    }

    @Override
    public int hashCode() {
        return Objects.hash(getIngredientId(), getIngredientName().replace(" ", ""));
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