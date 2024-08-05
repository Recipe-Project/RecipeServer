package com.recipe.app.src.etc.infra;

import com.recipe.app.src.common.infra.BaseRepositoryImpl;
import com.recipe.app.src.etc.domain.AppVersion;
import jakarta.persistence.EntityManager;

import static com.recipe.app.src.etc.domain.QAppVersion.*;


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
