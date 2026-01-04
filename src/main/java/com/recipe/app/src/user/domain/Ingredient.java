package com.recipe.app.src.user.domain;

import java.util.Random;

public enum Ingredient {
    POTATO("감자"),
    CARROT("당근"),
    ONION("양파"),
    SWEET_POTATO("고구마"),
    BROCCOLI("브로콜리"),
    BELL_PEPPER("피망"),
    TOMATO("토마토"),
    CHEESE("치즈"),
    EGG("계란"),
    RICE("밥"),
    MILK("우유"),
    BUTTER("버터"),
    SALT("소금"),
    SUGAR("설탕"),
    GARLIC("마늘"),
    GINGER("생강"),
    SCALLION("파"),
    LETTUCE("상추"),
    CUCUMBER("오이"),
    CHILI("고추"),
    SOYBEAN("콩"),
    BEEF("소고기"),
    PORK("돼지고기"),
    CHICKEN("닭고기"),
    FISH("생선"),
    SHRIMP("새우"),
    SQUID("오징어"),
    CLAM("조개"),
    MUSHROOM("버섯"),
    SPINACH("시금치"),
    CABBAGE("양배추"),
    CELERY("셀러리"),
    LEEK("대파"),
    RADISH("무"),
    BEET("비트"),
    CORN("옥수수"),
    PEA("완두콩"),
    ASPARAGUS("아스파라거스"),
    ZUCCHINI("주키니"),
    EGGPLANT("가지"),
    AVOCADO("아보카도"),
    LIME("라임"),
    LEMON("레몬"),
    ORANGE("오렌지"),
    APPLE("사과"),
    BANANA("바나나"),
    STRAWBERRY("딸기"),
    BLUEBERRY("블루베리"),
    MANGO("망고"),
    PINEAPPLE("파인애플");

    private final String koreanName;
    private static final Random RANDOM = new Random();

    Ingredient(String koreanName) {
        this.koreanName = koreanName;
    }

    public String getKoreanName() {
        return koreanName;
    }

    public static Ingredient getRandomIngredient() {
        Ingredient[] ingredients = Ingredient.values();
        return ingredients[RANDOM.nextInt(ingredients.length)];
    }
}
