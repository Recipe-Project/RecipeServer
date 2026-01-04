package com.recipe.app.src.user.domain;

import java.util.Random;

public enum Adjective {
    FRESH("신선한"),
    SPICY("매운"),
    SWEET("달콤한"),
    SAVORY("고소한"),
    SOFT("부드러운"),
    CRISPY("바삭한"),
    DEEP("진한"),
    LIGHT("가벼운"),
    SALTY("짭짤한"),
    SOUR("상큼한"),
    WARM("따뜻한"),
    COOL("차가운"),
    FRAGRANT("향기로운"),
    MILD("순한"),
    RICH("풍부한"),
    TENDER("연한"),
    JUICY("육즙많은"),
    CHEWY("쫄깃한"),
    FLAVORFUL("맛있는"),
    AROMATIC("향긋한"),
    THICK("농후한"),
    THIN("묽은"),
    HOT("뜨거운"),
    COLD("차가운"),
    TANGY("톡쏘는"),
    BITTER("쓴"),
    UMAMI("감칠맛나는"),
    PICKLED("절인"),
    SMOKED("훈제된"),
    TOASTED("구운"),
    ROASTED("로스트한"),
    STEAMED("쪄낸"),
    BRAISED("조린"),
    GRILLED("그릴구이"),
    FRIED("튀긴"),
    BAKED("구워진"),
    SIMMERED("졸인"),
    CHILLED("얼린"),
    CARAMELIZED("카라멜화된"),
    CREAMY("크리미한"),
    CRISP("아삭한"),
    BUTTERY("버터향의"),
    ZESTY("톡톡한"),
    NUTTY("견과향의"),
    SILKY("비단같은"),
    CRUNCHY("바삭바삭한"),
    FLUFFY("폭신한"),
    MOIST("촉촉한"),
    SMOKY("연기향의"),
    TENDER_MEAT("말랑한");

    private final String koreanName;
    private static final Random RANDOM = new Random();

    Adjective(String koreanName) {
        this.koreanName = koreanName;
    }

    public String getKoreanName() {
        return koreanName;
    }

    public static Adjective getRandomAdjective() {
        Adjective[] adjectives = Adjective.values();
        return adjectives[RANDOM.nextInt(adjectives.length)];
    }
}
