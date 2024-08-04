package com.recipe.app.common.infra;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;

public class BaseRepositoryImpl {

    protected final JPAQueryFactory queryFactory;

    public BaseRepositoryImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }
}
