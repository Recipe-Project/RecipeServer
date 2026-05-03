package com.recipe.app.src.common.utils;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.StringPath;

import java.util.function.BiFunction;
import java.util.function.Function;

public class QueryUtils {

    public static <T> BooleanExpression ifIdIsNotNullAndGreaterThanZero(BiFunction<Long, T, BooleanExpression> function, Long id, T condition) {
        return id != null && id > 0 ? function.apply(id, condition) : null;
    }

    public static BooleanExpression ifIdIsNotNullAndGreaterThanZero(Function<Long, BooleanExpression> function, Long id) {
        return id != null && id > 0 ? function.apply(id) : null;
    }

    /**
     * MySQL FULLTEXT BOOLEAN MODE 매칭 조건. SearchKeywordNormalizer 가 만든 BOOLEAN 모드 쿼리 문자열을 받는다.
     */
    public static BooleanExpression matchAgainst(StringPath column, String booleanQuery) {
        return Expressions.numberTemplate(Double.class,
                "function('match_against', {0}, {1})", column, booleanQuery).gt(0);
    }

    /**
     * 1글자 토큰 정확 매칭. FULLTEXT 인덱스의 ngram_token_size 제약 때문에 1글자는 BOOLEAN 모드로 잡지 못해
     * 공백 구분 단어 경계 LIKE 로 대체. 풀스캔이라 비용 있지만 1글자 검색 빈도 자체가 낮다고 가정.
     * 매칭 예: searchTokens="갓" 또는 "오 갓 김치" -> "갓" 매치, "갓김치"는 매치 안 됨.
     */
    public static BooleanExpression exactTokenMatch(StringPath column, String token) {
        return Expressions.booleanTemplate(
                "concat(' ', {0}, ' ') like concat('% ', {1}, ' %')",
                column, token);
    }

    /**
     * SearchQuery 타입에 맞춰 적절한 매칭 식을 반환. Empty 는 호출 직전 서비스에서 걸러져야 정상.
     */
    public static BooleanExpression matchSearchQuery(StringPath column, SearchKeywordNormalizer.SearchQuery query) {
        if (query instanceof SearchKeywordNormalizer.SearchQuery.BooleanQuery b) {
            return matchAgainst(column, b.query());
        }
        if (query instanceof SearchKeywordNormalizer.SearchQuery.ExactToken e) {
            return exactTokenMatch(column, e.token());
        }
        return Expressions.asBoolean(false).isTrue();
    }
}
