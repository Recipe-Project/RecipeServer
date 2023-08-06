package com.recipe.app.src.user.api;

import com.recipe.app.common.response.BaseResponse;
import com.recipe.app.common.utils.JwtService;
import com.recipe.app.src.user.application.UserService;
import com.recipe.app.src.user.application.dto.UserDto;
import com.recipe.app.src.user.domain.SecurityUser;
import com.recipe.app.src.user.domain.User;
import com.recipe.app.src.user.infra.UserEntity;
import com.recipe.app.src.user.exception.EmptyFcmTokenException;
import com.recipe.app.src.user.exception.EmptyTokenException;
import com.recipe.app.src.user.exception.ForbiddenUserException;
import com.recipe.app.src.user.exception.UserTokenNotExistException;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

import static com.recipe.app.common.response.BaseResponse.success;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

    private final UserService userService;
    private final JwtService jwtService;

    @Value("${header.naver-token}")
    private String naverToken;

    @Value("${header.kakao-token}")
    private String kakaoToken;

    @Value("${header.google-token}")
    private String googleToken;

    @Value("${header.fcm-token}")
    private String fcmToken;

    @Value("${jwt.token-header}")
    private String jwt;

    @ResponseBody
    @PostMapping("/auto-login")
    public BaseResponse<UserDto.UserProfileResponse> autoLogin(final Authentication authentication) {

        if (authentication == null)
            throw new UserTokenNotExistException();

        User user = ((SecurityUser) authentication.getPrincipal()).getUser();
        user = userService.autoLogin(user);
        UserDto.UserProfileResponse data = UserDto.UserProfileResponse.from(user);

        return success(data);
    }

    @ResponseBody
    @PostMapping("/naver-login")
    public BaseResponse<UserDto.UserProfileResponse> naverLogin(HttpServletRequest request) throws IOException, ParseException {

        String accessToken = request.getHeader(this.naverToken);
        if (!StringUtils.hasText(accessToken)) {
            throw new EmptyTokenException();
        }

        String fcmToken = request.getHeader(this.fcmToken);
        if (!StringUtils.hasText(fcmToken)) {
            throw new EmptyFcmTokenException();
        }

        User user = userService.naverLogin(accessToken, fcmToken);
        HttpHeaders httpHeaders = new HttpHeaders();
        String jwt = jwtService.createJwt(user.getUserId());
        httpHeaders.add(this.jwt, jwt);
        UserDto.UserProfileResponse data = UserDto.UserProfileResponse.from(user);

        return success(data);
    }

    @ResponseBody
    @PostMapping("/kakao-login")
    public BaseResponse<UserDto.UserProfileResponse> kakaoLogin(HttpServletRequest request) throws IOException, ParseException {
        String accessToken = request.getHeader(this.kakaoToken);
        if (!StringUtils.hasText(accessToken)) {
            throw new EmptyTokenException();
        }

        String fcmToken = request.getHeader(this.fcmToken);
        if (!StringUtils.hasText(fcmToken)) {
            throw new EmptyFcmTokenException();
        }

        User user = userService.kakaoLogin(accessToken, fcmToken);
        HttpHeaders httpHeaders = new HttpHeaders();
        String jwt = jwtService.createJwt(user.getUserId());
        httpHeaders.add(this.jwt, jwt);
        UserDto.UserProfileResponse data = UserDto.UserProfileResponse.from(user);

        return success(data);
    }

    @ResponseBody
    @PostMapping("/google-login")
    public BaseResponse<UserDto.UserProfileResponse> googleLogin(HttpServletRequest request) throws IOException, ParseException {
        String accessToken = request.getHeader(this.googleToken);
        if (!StringUtils.hasText(accessToken)) {
            throw new EmptyTokenException();
        }

        String fcmToken = request.getHeader(this.fcmToken);
        if (!StringUtils.hasText(fcmToken)) {
            throw new EmptyFcmTokenException();
        }

        User user = userService.googleLogin(accessToken, fcmToken);
        HttpHeaders httpHeaders = new HttpHeaders();
        String jwt = jwtService.createJwt(user.getUserId());
        httpHeaders.add(this.jwt, jwt);
        UserDto.UserProfileResponse data = UserDto.UserProfileResponse.from(user);

        return success(data);
    }

    @ResponseBody
    @GetMapping("")
    public BaseResponse<UserDto.UserProfileResponse> getUser(final Authentication authentication) {
        if (authentication == null)
            throw new UserTokenNotExistException();

        User user = ((SecurityUser) authentication.getPrincipal()).getUser();
        UserDto.UserProfileResponse data = UserDto.UserProfileResponse.from(user);

        return success(data);
    }

    @ResponseBody
    @PatchMapping("")
    public BaseResponse<UserDto.UserProfileResponse> patchUser(final Authentication authentication, @RequestBody UserDto.UserProfileRequest request) {

        if (authentication == null)
            throw new UserTokenNotExistException();

        User user = ((SecurityUser) authentication.getPrincipal()).getUser();
        user = userService.updateUser(user, request);
        UserDto.UserProfileResponse data = UserDto.UserProfileResponse.from(user);

        return success(data);
    }

    @ResponseBody
    @DeleteMapping("")
    public BaseResponse<Void> deleteUser(HttpServletRequest request, final Authentication authentication) {

        String jwt = jwtService.resolveToken(request);

        if (authentication == null || !StringUtils.hasText(jwt))
            throw new UserTokenNotExistException();

        User user = ((SecurityUser) authentication.getPrincipal()).getUser();

        userService.deleteUser(user, jwt);

        return success();
    }

    @ResponseBody
    @PostMapping("/logout")
    public BaseResponse<Void> logout(HttpServletRequest request) {
        String jwt = jwtService.resolveToken(request);

        if (!StringUtils.hasText(jwt))
            throw new UserTokenNotExistException();

        userService.registerJwtBlackList(jwt);

        return success();
    }
}
