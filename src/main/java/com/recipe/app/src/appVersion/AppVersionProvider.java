package com.recipe.app.src.appVersion;

import com.recipe.app.common.exception.BaseException;
import com.recipe.app.src.appVersion.models.AppVersion;
import com.recipe.app.src.appVersion.models.GetAppVersionRes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AppVersionProvider {
    private final AppVersionRepositiory appVersionRepositiory;

    @Autowired
    public AppVersionProvider(AppVersionRepositiory appVersionRepositiory) {
        this.appVersionRepositiory = appVersionRepositiory;
    }


    /**
     * 앱 버전 조회 API
     *
     * @return GetAppVersionRes
     * @throws BaseException
     */
    @Transactional
    public GetAppVersionRes retrieveAppVersion() throws BaseException {
        AppVersion appVersion = appVersionRepositiory.findByIdx(1);

        return new GetAppVersionRes(appVersion.getVersion());
    }
}
