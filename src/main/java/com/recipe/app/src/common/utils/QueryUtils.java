package com.recipe.app.src.common.utils;

import com.querydsl.core.types.dsl.BooleanExpression;

import java.util.function.BiFunction;
import java.util.function.Function;

public class QueryUtils {

    public static <T> BooleanExpression ifIdIsNotNullAndGreaterThanZero(BiFunction<Long, T, BooleanExpression> function, Long id, T condition) {
        return id != null && id > 0 ? function.apply(id, condition) : null;
    }

    public static BooleanExpression ifIdIsNotNullAndGreaterThanZero(Function<Long, BooleanExpression> function, Long id) {
        return id != null && id > 0 ? function.apply(id) : null;
    }
}
