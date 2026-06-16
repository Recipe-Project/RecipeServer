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
     *     <li>{@link SearchQuery.BooleanQuery}: 2글자 이상. nori 토큰화 후 공백 구분 OR BOOLEAN 모드
     *         (연산자 없는 토큰 나열 = OR). AND(+token) 가 아니라 OR 인 이유: 합성어가 nori 로 쪼개질 때
     *         (예 "닭가슴살" -> "닭 가슴살") 1글자 토큰("닭")이 FULLTEXT 최소 토큰 길이(2) 미만이라
     *         AND 필수조건으로 들어가면 영영 매칭 불가가 되어 전체 결과가 0건이 된다. OR 로 두면
     *         인덱싱 불가능한 1글자 토큰은 자연히 무시되고 나머지 토큰으로 매칭된다.</li>
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
                .collect(Collectors.joining(" "));

        return boolQuery.isBlank() ? new SearchQuery.Empty() : new SearchQuery.BooleanQuery(boolQuery);
    }

    public sealed interface SearchQuery {
        record Empty() implements SearchQuery {}
        record ExactToken(String token) implements SearchQuery {}
        record BooleanQuery(String query) implements SearchQuery {}
    }
}
