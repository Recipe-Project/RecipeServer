package com.recipe.app.src.user.api;

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
import com.recipe.app.src.user.domain.SecurityUser;
import com.recipe.app.src.user.domain.User;
import com.recipe.app.src.user.exception.UserTokenNotExistException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.json.simple.parser.ParseException;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@Api(tags = {"유저 Controller"})
@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;
    private final UserFacadeService userFacadeService;

    public UserController(UserService userService, UserFacadeService userFacadeService) {

        this.userService = userService;
        this.userFacadeService = userFacadeService;
    }

    @ApiOperation(value = "토큰 재발급 API")
    @PostMapping("/token-reissue")
    public UserTokenRefreshResponse reissueToken(@RequestBody UserTokenRefreshRequest request) {

        return userService.reissueToken(request);
    }

    @ApiOperation(value = "자동 로그인 API")
    @PostMapping("/auto-login")
    public UserLoginResponse autoLogin(@ApiIgnore final Authentication authentication) {

        if (authentication == null)
            throw new UserTokenNotExistException();

        User user = ((SecurityUser) authentication.getPrincipal()).getUser();

        return userService.autoLogin(user);
    }

    @ApiOperation(value = "네이버 로그인 API")
    @PostMapping("/naver-login")
    public UserSocialLoginResponse naverLogin(@ApiParam(value = "로그인 요청 정보", required = true) @RequestBody UserLoginRequest request) throws IOException, ParseException {

        return userService.naverLogin(request);
    }

    @ApiOperation(value = "카카오 로그인 API")
    @PostMapping("/kakao-login")
    public UserSocialLoginResponse kakaoLogin(@ApiParam(value = "로그인 요청 정보", required = true) @RequestBody UserLoginRequest request) throws IOException, ParseException {

        return userService.kakaoLogin(request);
    }

    @ApiOperation(value = "구글 로그인 API")
    @PostMapping("/google-login")
    public UserSocialLoginResponse googleLogin(@ApiParam(value = "로그인 요청 정보", required = true) @RequestBody UserLoginRequest request) throws IOException, ParseException {

        return userService.googleLogin(request);
    }

    @ApiOperation(value = "유저 프로필 조회 API")
    @GetMapping
    public UserProfileResponse getUser(@ApiIgnore final Authentication authentication) {

        if (authentication == null)
            throw new UserTokenNotExistException();

        User user = ((SecurityUser) authentication.getPrincipal()).getUser();

        return userFacadeService.findUserProfile(user);
    }

    @ApiOperation(value = "유저 프로필 수정 API")
    @PatchMapping
    public void patchUser(@ApiIgnore final Authentication authentication,
                          @ApiParam(value = "수정할 회원 정보", required = true)
                          @RequestBody UserProfileRequest request) {

        if (authentication == null)
            throw new UserTokenNotExistException();

        User user = ((SecurityUser) authentication.getPrincipal()).getUser();

        userService.updateUser(user, request);
    }

    @ApiOperation(value = "회원 탈퇴 API")
    @DeleteMapping
    public void deleteUser(HttpServletRequest request, @ApiIgnore final Authentication authentication) {

        if (authentication == null)
            throw new UserTokenNotExistException();

        User user = ((SecurityUser) authentication.getPrincipal()).getUser();

        userFacadeService.deleteUser(user, request);
    }

    @ApiOperation(value = "로그아웃 API")
    @PostMapping("/logout")
    public void logout(HttpServletRequest request, @ApiIgnore final Authentication authentication) {

        if (authentication == null)
            throw new UserTokenNotExistException();

        userService.logout(request);
    }

    @ApiOperation(value = "FCM 디바이스 토큰 수정 API")
    @PatchMapping("/fcm-token")
    public void patchFcmToken(@ApiIgnore final Authentication authentication,
                              @ApiParam(value = "FCM 디바이스 토큰 정보", required = true)
                              @RequestBody UserDeviceTokenRequest request) {

        if (authentication == null)
            throw new UserTokenNotExistException();

        User user = ((SecurityUser) authentication.getPrincipal()).getUser();

        userService.updateFcmToken(user, request);
    }
}
