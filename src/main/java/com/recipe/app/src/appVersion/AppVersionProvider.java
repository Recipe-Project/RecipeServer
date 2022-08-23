package com.recipe.app.src.appVersion;

import com.recipe.app.config.BaseException;
import com.recipe.app.src.appVersion.models.AppVersion;
import com.recipe.app.src.appVersion.models.GetAppVersionRes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.recipe.app.config.BaseResponseStatus.FAILED_TO_GET_APP_VERSION;

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
        AppVersion appVersion;
        try {
            appVersion = appVersionRepositiory.findByIdx(1);
        } catch (Exception ignored) {
            throw new BaseException(FAILED_TO_GET_APP_VERSION);
        }

        return new GetAppVersionRes(appVersion.getVersion());
    }
}
