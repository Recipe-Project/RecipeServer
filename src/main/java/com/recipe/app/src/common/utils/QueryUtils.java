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
}
