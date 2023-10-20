package com.recipe.app.src.ingredient.domain;

import com.recipe.app.src.user.domain.User;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Getter
public class Ingredient {

    private final Long ingredientId;
    private final IngredientCategory ingredientCategory;
    private final String ingredientName;
    private final String ingredientIconUrl;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;
    private final User user;
    private final boolean isDefault;
    private final boolean isHidden;

    @Builder
    public Ingredient(Long ingredientId, IngredientCategory ingredientCategory, String ingredientName, String ingredientIconUrl, LocalDateTime createdAt, LocalDateTime updatedAt,
                      User user, boolean isDefault, boolean isHidden) {
        this.ingredientId = ingredientId;
        this.ingredientCategory = ingredientCategory;
        this.ingredientName = ingredientName;
        this.ingredientIconUrl = ingredientIconUrl;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.user = user;
        this.isDefault = isDefault;
        this.isHidden = isHidden;
    }

    public static Ingredient from(IngredientCategory ingredientCategory, String ingredientName, String ingredientIconUrl, User user) {
        LocalDateTime now = LocalDateTime.now();
        return Ingredient.builder()
                .ingredientCategory(ingredientCategory)
                .ingredientName(ingredientName)
                .ingredientIconUrl(ingredientIconUrl)
                .createdAt(now)
                .updatedAt(now)
                .user(user)
                .isDefault(false)
                .isHidden(true)
                .build();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Ingredient ingredient = (Ingredient) o;

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
        if(ingredientName.equals("새우"))
            return List.of("대하");
        if(ingredientName.equals("대하"))
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
        if(ingredientName.equals("고춧가루"))
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
