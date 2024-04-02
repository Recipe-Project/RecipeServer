package com.recipe.app.src.user.api;

import com.recipe.app.common.response.BaseResponse;
import com.recipe.app.common.utils.JwtService;
import com.recipe.app.src.user.application.UserFacadeService;
import com.recipe.app.src.user.application.UserService;
import com.recipe.app.src.user.application.dto.UserDeviceTokenRequest;
import com.recipe.app.src.user.application.dto.UserLoginRequest;
import com.recipe.app.src.user.application.dto.UserLoginResponse;
import com.recipe.app.src.user.application.dto.UserProfileRequest;
import com.recipe.app.src.user.application.dto.UserProfileResponse;
import com.recipe.app.src.user.application.dto.UserSocialLoginResponse;
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

import static com.recipe.app.common.response.BaseResponse.success;

@Api(tags = {"유저 Controller"})
@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;
    private final JwtService jwtService;
    private final UserFacadeService userFacadeService;

    public UserController(UserService userService, JwtService jwtService, UserFacadeService userFacadeService) {

        this.userService = userService;
        this.jwtService = jwtService;
        this.userFacadeService = userFacadeService;
    }

    @ApiOperation(value = "자동 로그인 API")
    @PostMapping("/auto-login")
    public BaseResponse<UserLoginResponse> autoLogin(@ApiIgnore final Authentication authentication) {

        if (authentication == null)
            throw new UserTokenNotExistException();

        User user = ((SecurityUser) authentication.getPrincipal()).getUser();

        return success(userService.autoLogin(user));
    }

    @ApiOperation(value = "네이버 로그인 API")
    @PostMapping("/naver-login")
    public BaseResponse<UserSocialLoginResponse> naverLogin(@ApiParam(value = "로그인 요청 정보", required = true) @RequestBody UserLoginRequest request) throws IOException, ParseException {

        return success(userService.naverLogin(request));
    }

    @ApiOperation(value = "카카오 로그인 API")
    @PostMapping("/kakao-login")
    public BaseResponse<UserSocialLoginResponse> kakaoLogin(@ApiParam(value = "로그인 요청 정보", required = true) @RequestBody UserLoginRequest request) throws IOException, ParseException {

        return success(userService.kakaoLogin(request));
    }

    @ApiOperation(value = "구글 로그인 API")
    @PostMapping("/google-login")
    public BaseResponse<UserSocialLoginResponse> googleLogin(@ApiParam(value = "로그인 요청 정보", required = true) @RequestBody UserLoginRequest request) throws IOException, ParseException {

        return success(userService.googleLogin(request));
    }

    @ApiOperation(value = "유저 프로필 조회 API")
    @GetMapping
    public BaseResponse<UserProfileResponse> getUser(@ApiIgnore final Authentication authentication) {

        if (authentication == null)
            throw new UserTokenNotExistException();

        User user = ((SecurityUser) authentication.getPrincipal()).getUser();

        return success(userFacadeService.findUserProfile(user));
    }

    @ApiOperation(value = "유저 프로필 수정 API")
    @PatchMapping
    public BaseResponse<Void> patchUser(@ApiIgnore final Authentication authentication,
                                                               @ApiParam(value = "수정할 회원 정보", required = true)
                                                               @RequestBody UserProfileRequest request) {

        if (authentication == null)
            throw new UserTokenNotExistException();

        User user = ((SecurityUser) authentication.getPrincipal()).getUser();
        userService.updateUser(user, request);

        return success();
    }

    @ApiOperation(value = "회원 탈퇴 API")
    @DeleteMapping
    public BaseResponse<Void> deleteUser(HttpServletRequest request, @ApiIgnore final Authentication authentication) {

        if (authentication == null)
            throw new UserTokenNotExistException();

        User user = ((SecurityUser) authentication.getPrincipal()).getUser();
        userFacadeService.deleteUser(user, request);

        return success();
    }

    @ApiOperation(value = "로그아웃 API")
    @PostMapping("/logout")
    public BaseResponse<Void> logout(HttpServletRequest request) {

        jwtService.createJwtBlacklist(request);

        return success();
    }

    @ApiOperation(value = "FCM 디바이스 토큰 수정 API")
    @PatchMapping("/fcm-token")
    public BaseResponse<Void> patchFcmToken(@ApiIgnore final Authentication authentication,
                                            @ApiParam(value = "FCM 디바이스 토큰 정보", required = true)
                                            @RequestBody UserDeviceTokenRequest request) {

        if (authentication == null)
            throw new UserTokenNotExistException();

        User user = ((SecurityUser) authentication.getPrincipal()).getUser();
        userService.updateFcmToken(user, request);

        return success();
    }
}
