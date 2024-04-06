package com.recipe.app.common.utils;

import com.querydsl.core.types.dsl.BooleanExpression;

import java.util.function.BiFunction;

public class QueryUtils {

    public static <T> BooleanExpression ifIdIsNotNullAndGreaterThanZero(BiFunction<Long, T, BooleanExpression> function, Long id, T condition) {
        return id != null && id > 0 ? function.apply(id, condition) : null;
    }
}
