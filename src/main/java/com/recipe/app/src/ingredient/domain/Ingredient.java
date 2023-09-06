package com.recipe.app.src.ingredient.domain;

import com.recipe.app.src.user.domain.User;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
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
        if (!(o instanceof Ingredient))
            return false;
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
        return Objects.hash(ingredientId, ingredientName.replace(" ", ""));
    }
}
