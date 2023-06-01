package com.recipe.app.src.user.api;

import com.recipe.app.common.exception.BaseException;
import com.recipe.app.common.response.BaseResponse;
import com.recipe.app.src.user.application.UserProvider;
import com.recipe.app.src.user.application.UserService;
import com.recipe.app.src.user.application.dto.GetUserRes;
import com.recipe.app.src.user.application.dto.PatchUserReq;
import com.recipe.app.src.user.application.dto.PatchUserRes;
import com.recipe.app.src.user.application.dto.PostUserRes;
import com.recipe.app.common.utils.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

import static com.recipe.app.common.response.BaseResponse.*;
import static com.recipe.app.common.response.BaseResponseStatus.*;



@RestController
@RequestMapping("/users")
public class UserController {
    private final UserProvider userProvider;
    private final UserService userService;
    private final JwtService jwtService;

    @Autowired
    public UserController(UserProvider userProvider, UserService userService, JwtService jwtService) {
        this.userProvider = userProvider;
        this.userService = userService;
        this.jwtService = jwtService;
    }


    /**
     * 자동로그인 API
     * [POST] /users/auto-login
     */
    @ResponseBody
    @PostMapping("/auto-login")
    public BaseResponse<Void> postAutoLogin() {
        Integer userIdx = jwtService.getUserId();
        userProvider.retrieveUserByUserIdx(userIdx);
        return success();
    }

    /**
     * 네이버 로그인 API
     * [POST] /users/naver-login
     * @return BaseResponse<PostUserRes>
     */
    @ResponseBody
    @PostMapping("/naver-login")
    public BaseResponse<PostUserRes> postNaverLogin() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        String accessToken = request.getHeader("NAVER-ACCESS-TOKEN");
        String fcmToken = request.getHeader("FCM-TOKEN");
        if (accessToken == null || accessToken.length() == 0) {
            throw new BaseException(EMPTY_TOKEN);
        }
        if (fcmToken == null || fcmToken.length() == 0) {
            throw new BaseException(EMPTY_FCM_TOKEN);
        }
        PostUserRes postUserRes = userService.naverLogin(accessToken,fcmToken);
        return success(postUserRes);
    }


    /**
     * 카카오 로그인 API
     * [POST] /users/kakao-login
     * @return BaseResponse<PostUserRes>
     */
    @ResponseBody
    @PostMapping("/kakao-login")
    public BaseResponse<PostUserRes> postKakaoLogin() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        String accessToken = request.getHeader("KAKAO-ACCESS-TOKEN");
        String fcmToken = request.getHeader("FCM-TOKEN");
        if (accessToken == null || accessToken.length() == 0) {
            throw new BaseException(EMPTY_TOKEN);
        }
        if (fcmToken == null || fcmToken.length() == 0) {
            throw new BaseException(EMPTY_FCM_TOKEN);
        }
        PostUserRes postUserRes = userService.kakaoLogin(accessToken,fcmToken);
        return success(postUserRes);
    }
    /**
     * 구글 로그인 API
     * [POST] /users/google-login
     * @RequestBody parameters (accesstoken)
     */
    @ResponseBody
    @PostMapping("/google-login")
    public BaseResponse<PostUserRes> postGoogleLogin() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        String googleIdToken = request.getHeader("GOOGLE-ID-TOKEN");
        String fcmToken = request.getHeader("FCM-TOKEN");

        if (googleIdToken == null || googleIdToken.length() == 0) {
            throw new BaseException(EMPTY_TOKEN);
        }
        if (fcmToken == null || fcmToken.length() == 0) {
            throw new BaseException(EMPTY_FCM_TOKEN);
        }

        PostUserRes postUserRes = userService.googleLogin(googleIdToken,fcmToken);
        return success(postUserRes);
    }

    /**
     * 마이페이지 조회 API
     * [GET] /users/:userIdx
     * @return BaseResponse<GetUserRes>
     */
    @ResponseBody
    @GetMapping("/{userIdx}")
    public BaseResponse<GetUserRes> getUser(@PathVariable Integer userIdx) {
        if(userIdx==null || userIdx<=0){
            throw new BaseException(USERS_EMPTY_USER_ID);
        }
        int jwtUserIdx = jwtService.getUserId();
        GetUserRes getUserRes = userProvider.retrieveUser(jwtUserIdx, userIdx);
        return success(getUserRes);
    }

    /**
     * 회원 정보 수정 API
     * [PATCH] /users/:userIdx
     * @return BaseResponse<PatchUserRes>
     */
    @ResponseBody
    @PatchMapping("/{userIdx}")
    public BaseResponse<PatchUserRes> patchUser(@PathVariable Integer userIdx, @RequestBody PatchUserReq parameters) {
        if(userIdx==null || userIdx<=0){
            throw new BaseException(USERS_EMPTY_USER_ID);
        }

        int jwtUserIdx = jwtService.getUserId();

        PatchUserRes patchUserRes = userService.updateUser(jwtUserIdx, userIdx, parameters);
        return success(patchUserRes);
    }

    /**
     * 회원 탈퇴 API
     * [DELETE] /users/:userIdx
     */
    @ResponseBody
    @DeleteMapping("/{userIdx}")
    public BaseResponse<Void> deleteUser(@PathVariable Integer userIdx) {
        if(userIdx==null || userIdx<=0){
            throw new BaseException(USERS_EMPTY_USER_ID);
        }

        int jwtUserIdx = jwtService.getUserId();

        userService.deleteUser(jwtUserIdx, userIdx);
        return success();
    }


}
