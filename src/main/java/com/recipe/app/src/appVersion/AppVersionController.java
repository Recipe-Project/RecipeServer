package com.recipe.app.src.appVersion;

import com.recipe.app.common.exception.BaseException;
import com.recipe.app.common.response.BaseResponse;
import com.recipe.app.src.appVersion.models.GetAppVersionRes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("")
public class AppVersionController {
    private final AppVersionProvider appVersionProvider;

    @Autowired
    public AppVersionController(AppVersionProvider appVersionProvider) {
        this.appVersionProvider = appVersionProvider;
    }

    /**
     * 앱 버전 조회 API
     * [GET] /app/version
     *
     * @return BaseResponse<GetAppVersionRes>
     */
    @GetMapping("/app/version")
    public BaseResponse<GetAppVersionRes> getAppVersion() throws BaseException {

        try {
            GetAppVersionRes getAppVersionRes = appVersionProvider.retrieveAppVersion();
            return new BaseResponse<>(getAppVersionRes);
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }
}
