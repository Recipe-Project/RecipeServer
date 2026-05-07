package com.recipe.app.src.common.utils;

import java.util.Arrays;
import java.util.stream.Collectors;

public final class SearchKeywordNormalizer {

    private SearchKeywordNormalizer() {
    }

    /**
     * 사용자 입력을 검색 의도에 맞는 쿼리 형태로 변환한다.
     * <ul>
     *     <li>{@link SearchQuery.Empty}: 입력이 비었거나 토큰화 결과가 빈 경우. 호출자는 검색 자체를 스킵</li>
     *     <li>{@link SearchQuery.ExactToken}: 1글자 입력. FULLTEXT BOOLEAN 모드는 토큰 최소 길이 제약 때문에
     *         사용 불가하므로 단어 경계 정확 매칭으로 처리 (예: "갓","닭","감")</li>
     *     <li>{@link SearchQuery.BooleanQuery}: 2글자 이상. nori 토큰화 후 +token AND BOOLEAN 모드</li>
     * </ul>
     */
    public static SearchQuery normalize(String rawKeyword) {

        if (rawKeyword == null) return new SearchQuery.Empty();

        String stripped = rawKeyword
                .replaceAll("[+\\-><()~*\"@]", " ")
                .trim()
                .replaceAll("\\s+", " ")
                .toLowerCase();

        if (stripped.isEmpty()) return new SearchQuery.Empty();

        if (stripped.length() == 1) return new SearchQuery.ExactToken(stripped);

        String tokenized = KoreanTokenizer.tokenize(stripped);
        if (tokenized.isBlank()) return new SearchQuery.Empty();

        String[] tokens = tokenized.split(" ");
        String boolQuery = Arrays.stream(tokens)
                .filter(t -> !t.isBlank())
                .map(t -> "+" + t)
                .collect(Collectors.joining(" "));

        return boolQuery.isBlank() ? new SearchQuery.Empty() : new SearchQuery.BooleanQuery(boolQuery);
    }

    public sealed interface SearchQuery {
        record Empty() implements SearchQuery {}
        record ExactToken(String token) implements SearchQuery {}
        record BooleanQuery(String query) implements SearchQuery {}
    }
}
