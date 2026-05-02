package com.recipe.app.src.common.config;

import org.hibernate.boot.model.FunctionContributions;
import org.hibernate.boot.model.FunctionContributor;
import org.hibernate.type.StandardBasicTypes;

/**
 * Hibernate 에 MySQL FULLTEXT MATCH AGAINST 함수를 등록한다.
 * QueryDSL 등에서 function('match_against', col, query) 로 호출 가능.
 */
public class MysqlFulltextFunctionContributor implements FunctionContributor {

    @Override
    public void contributeFunctions(FunctionContributions functionContributions) {

        functionContributions.getFunctionRegistry()
                .registerPattern(
                        "match_against",
                        "MATCH(?1) AGAINST(?2 IN BOOLEAN MODE)",
                        functionContributions.getTypeConfiguration()
                                .getBasicTypeRegistry()
                                .resolve(StandardBasicTypes.DOUBLE)
                );
    }
}
