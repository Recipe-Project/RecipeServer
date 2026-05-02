package com.recipe.app.src.common.utils;

import java.util.Arrays;
import java.util.stream.Collectors;

public final class SearchKeywordNormalizer {

    private static final int MIN_KEYWORD_LENGTH = 2;

    private SearchKeywordNormalizer() {
    }

    /**
     * 사용자 입력을 BOOLEAN MODE FULLTEXT 검색식으로 변환한다.
     * <ul>
     *     <li>입력이 너무 짧거나 토큰이 비어있으면 null 반환 (호출자는 검색 자체를 스킵)</li>
     *     <li>nori 토큰화 결과를 +token 형태로 묶어 AND 매칭한다</li>
     * </ul>
     */
    public static String toBooleanModeQuery(String rawKeyword) {

        if (rawKeyword == null) return null;

        String stripped = rawKeyword
                .replaceAll("[+\\-><()~*\"@]", " ")
                .trim()
                .replaceAll("\\s+", " ");

        if (stripped.length() < MIN_KEYWORD_LENGTH) return null;

        String tokenized = KoreanTokenizer.tokenize(stripped);
        if (tokenized.isBlank()) return null;

        String[] tokens = tokenized.split(" ");
        String query = Arrays.stream(tokens)
                .filter(t -> !t.isBlank())
                .map(t -> "+" + t)
                .collect(Collectors.joining(" "));

        return query.isBlank() ? null : query;
    }
}
