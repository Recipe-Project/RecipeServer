package com.recipe.app.src.user.api;

import com.recipe.app.common.response.BaseResponse;
import com.recipe.app.common.utils.JwtService;
import com.recipe.app.src.user.application.UserService;
import com.recipe.app.src.user.application.dto.UserDto;
import com.recipe.app.src.user.domain.User;
import com.recipe.app.src.user.exception.EmptyFcmTokenException;
import com.recipe.app.src.user.exception.EmptyTokenException;
import com.recipe.app.src.user.exception.ForbiddenUserException;
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
    public BaseResponse<Void> postAutoLogin(final Authentication authentication) {
        Integer userIdx = ((User) authentication.getPrincipal()).getUserIdx();
        userService.retrieveUserByUserIdx(userIdx);

        return success();
    }

    @ResponseBody
    @PostMapping("/naver-login")
    public BaseResponse<UserDto.UserProfileResponse> postNaverLogin(HttpServletRequest request) throws IOException, ParseException {
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
        String jwt = jwtService.createJwt(user.getUserIdx());
        httpHeaders.add(this.jwt, jwt);
        UserDto.UserProfileResponse data = new UserDto.UserProfileResponse(user);

        return success(data);
    }

    @ResponseBody
    @PostMapping("/kakao-login")
    public BaseResponse<UserDto.UserProfileResponse> postKakaoLogin(HttpServletRequest request) throws IOException, ParseException {
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
        String jwt = jwtService.createJwt(user.getUserIdx());
        httpHeaders.add(this.jwt, jwt);
        UserDto.UserProfileResponse data = new UserDto.UserProfileResponse(user);

        return success(data);
    }

    @ResponseBody
    @PostMapping("/google-login")
    public BaseResponse<UserDto.UserProfileResponse> postGoogleLogin(HttpServletRequest request) throws IOException, ParseException {
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
        String jwt = jwtService.createJwt(user.getUserIdx());
        httpHeaders.add(this.jwt, jwt);
        UserDto.UserProfileResponse data = new UserDto.UserProfileResponse(user);

        return success(data);
    }

    @ResponseBody
    @GetMapping("/{userIdx}")
    public BaseResponse<UserDto.UserProfileResponse> getUser(final Authentication authentication, @PathVariable Integer userIdx) {
        int jwtUserIdx = ((User) authentication.getPrincipal()).getUserIdx();
        if (!userIdx.equals(jwtUserIdx)) {
            throw new ForbiddenUserException();
        }

        User user = userService.retrieveUserByUserIdx(userIdx);
        HttpHeaders httpHeaders = new HttpHeaders();
        String jwt = jwtService.createJwt(user.getUserIdx());
        httpHeaders.add(this.jwt, jwt);
        UserDto.UserProfileResponse data = new UserDto.UserProfileResponse(user);

        return success(data);
    }

    @ResponseBody
    @PatchMapping("/{userIdx}")
    public BaseResponse<UserDto.UserProfileResponse> patchUser(final Authentication authentication, @PathVariable int userIdx, @RequestBody UserDto.UserProfileRequest request) {
        int jwtUserIdx = ((User) authentication.getPrincipal()).getUserIdx();
        if (userIdx != jwtUserIdx) {
            throw new ForbiddenUserException();
        }

        UserDto.UserProfileResponse data = new UserDto.UserProfileResponse(userService.updateUser(userIdx, request));

        return success(data);
    }

    @ResponseBody
    @DeleteMapping("/{userIdx}")
    public BaseResponse<Void> deleteUser(HttpServletRequest request, final Authentication authentication, @PathVariable int userIdx) {
        int jwtUserIdx = ((User) authentication.getPrincipal()).getUserIdx();
        if (userIdx != jwtUserIdx) {
            throw new ForbiddenUserException();
        }

        userService.deleteUser(userIdx);
        String jwt = jwtService.resolveToken(request);
        userService.registerJwtBlackList(jwt);

        return success();
    }


}
