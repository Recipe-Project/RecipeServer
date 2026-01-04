package com.recipe.app.src.user.domain;

public class NicknameGenerator {

    private NicknameGenerator() {
        // Utility class
    }

    /**
     * 형용사 + 재료명 + 나노초 기반 숫자로 닉네임을 생성합니다.
     * 예시: 신선한감자35980622
     */
    public static String generate() {
        Adjective adjective = Adjective.getRandomAdjective();
        Ingredient ingredient = Ingredient.getRandomIngredient();
        String uniqueId = String.valueOf(System.nanoTime()).substring(5, 13);

        return adjective.getKoreanName() + ingredient.getKoreanName() + uniqueId;
    }
}
