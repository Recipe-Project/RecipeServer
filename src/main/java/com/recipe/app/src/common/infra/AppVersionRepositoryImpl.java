package com.recipe.app.src.common.infra;

import com.recipe.app.common.infra.BaseRepositoryImpl;
import com.recipe.app.src.common.domain.AppVersion;

import javax.persistence.EntityManager;

import static com.recipe.app.src.common.domain.QAppVersion.appVersion;

public class AppVersionRepositoryImpl extends BaseRepositoryImpl implements AppVersionCustomRepository {

    public AppVersionRepositoryImpl(EntityManager em) {
        super(em);
    }


    @Override
    public AppVersion findRecentAppVersion() {

        return queryFactory
                .selectFrom(appVersion)
                .orderBy(appVersion.appVersionId.desc())
                .fetchFirst();
    }
}
