package com.recipe.app.src.common.infra;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;

public class BaseRepositoryImpl {

    protected final JPAQueryFactory queryFactory;

    public BaseRepositoryImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }
}
