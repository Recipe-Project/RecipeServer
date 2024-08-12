package com.recipe.app.src.user.api;

import com.recipe.app.src.common.aop.LoginCheck;
import com.recipe.app.src.user.application.UserFacadeService;
import com.recipe.app.src.user.application.UserService;
import com.recipe.app.src.user.application.dto.UserDeviceTokenRequest;
import com.recipe.app.src.user.application.dto.UserLoginRequest;
import com.recipe.app.src.user.application.dto.UserLoginResponse;
import com.recipe.app.src.user.application.dto.UserProfileRequest;
import com.recipe.app.src.user.application.dto.UserProfileResponse;
import com.recipe.app.src.user.application.dto.UserSocialLoginResponse;
import com.recipe.app.src.user.application.dto.UserTokenRefreshRequest;
import com.recipe.app.src.user.application.dto.UserTokenRefreshResponse;
import com.recipe.app.src.user.domain.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "유저 Controller")
@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;
    private final UserFacadeService userFacadeService;

    public UserController(UserService userService, UserFacadeService userFacadeService) {

        this.userService = userService;
        this.userFacadeService = userFacadeService;
    }

    @Operation(summary = "토큰 재발급 API")
    @PostMapping("/token-reissue")
    public UserTokenRefreshResponse reissueToken(@RequestBody UserTokenRefreshRequest request) {

        return userService.reissueToken(request);
    }

    @Operation(summary = "자동 로그인 API")
    @PostMapping("/auto-login")
    @LoginCheck
    public UserLoginResponse autoLogin(@Parameter(hidden = true) User user) {

        return userService.autoLogin(user);
    }

    @Operation(summary = "네이버 로그인 API")
    @PostMapping("/naver-login")
    public UserSocialLoginResponse naverLogin(@Parameter(name = "로그인 요청 정보", required = true)
                                              @RequestBody UserLoginRequest request) {

        return userService.naverLogin(request);
    }

    @Operation(summary = "카카오 로그인 API")
    @PostMapping("/kakao-login")
    public UserSocialLoginResponse kakaoLogin(@Parameter(name = "로그인 요청 정보", required = true)
                                              @RequestBody UserLoginRequest request) {

        return userService.kakaoLogin(request);
    }

    @Operation(summary = "구글 로그인 API")
    @PostMapping("/google-login")
    public UserSocialLoginResponse googleLogin(@Parameter(name = "로그인 요청 정보", required = true)
                                               @RequestBody UserLoginRequest request) {

        return userService.googleLogin(request);
    }

    @Operation(summary = "유저 프로필 조회 API")
    @GetMapping
    @LoginCheck
    public UserProfileResponse getUser(@Parameter(hidden = true) User user) {

        return userFacadeService.findUserProfile(user);
    }

    @Operation(summary = "유저 프로필 수정 API")
    @PatchMapping
    @LoginCheck
    public void patchUser(@Parameter(hidden = true) User user,
                          @Parameter(name = "수정할 회원 정보", required = true)
                          @RequestBody UserProfileRequest request) {

        userService.update(user, request);
    }

    @Operation(summary = "회원 탈퇴 API")
    @DeleteMapping
    @LoginCheck
    public void deleteUser(HttpServletRequest request, @Parameter(hidden = true) User user) {

        userFacadeService.deleteUser(user, request);
    }

    @Operation(summary = "로그아웃 API")
    @PostMapping("/logout")
    @LoginCheck
    public void logout(HttpServletRequest request, @Parameter(hidden = true) User user) {

        userService.logout(request);
    }

    @Operation(summary = "FCM 디바이스 토큰 수정 API")
    @PatchMapping("/fcm-token")
    @LoginCheck
    public void patchFcmToken(@Parameter(hidden = true) User user,
                              @Parameter(name = "FCM 디바이스 토큰 정보", required = true)
                              @RequestBody UserDeviceTokenRequest request) {

        userService.updateFcmToken(user, request);
    }
}
