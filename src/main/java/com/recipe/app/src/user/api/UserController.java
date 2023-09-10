package com.recipe.app.src.user.api;

import com.recipe.app.common.response.BaseResponse;
import com.recipe.app.common.utils.JwtService;
import com.recipe.app.src.recipe.application.BlogRecipeService;
import com.recipe.app.src.recipe.application.RecipeService;
import com.recipe.app.src.recipe.application.YoutubeRecipeService;
import com.recipe.app.src.recipe.domain.Recipe;
import com.recipe.app.src.user.application.UserService;
import com.recipe.app.src.user.application.dto.UserDto;
import com.recipe.app.src.user.domain.SecurityUser;
import com.recipe.app.src.user.domain.User;
import com.recipe.app.src.user.exception.EmptyFcmTokenException;
import com.recipe.app.src.user.exception.EmptyTokenException;
import com.recipe.app.src.user.exception.UserTokenNotExistException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;

import static com.recipe.app.common.response.BaseResponse.success;

@Api(tags = {"유저 Controller"})
@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

    private final UserService userService;
    private final RecipeService recipeService;
    private final YoutubeRecipeService youtubeRecipeService;
    private final BlogRecipeService blogRecipeService;
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

    @ApiOperation(value = "자동 로그인 API")
    @ResponseBody
    @PostMapping("/auto-login")
    public BaseResponse<UserDto.UserProfileResponse> autoLogin(@ApiIgnore final Authentication authentication) {

        if (authentication == null)
            throw new UserTokenNotExistException();

        User user = ((SecurityUser) authentication.getPrincipal()).getUser();
        user = userService.autoLogin(user);
        long youtubeScrapCnt = youtubeRecipeService.countYoutubeScrapByUser(user);
        long blogScrapCnt = blogRecipeService.countBlogScrapByUser(user);
        long recipeScrapCnt = recipeService.countRecipeScrapByUser(user);
        List<Recipe> userRecipes = recipeService.getRecipesByUser(user);
        UserDto.UserProfileResponse data = UserDto.UserProfileResponse.from(user, userRecipes, youtubeScrapCnt, blogScrapCnt, recipeScrapCnt);

        return success(data);
    }

    @ApiOperation(value = "네이버 로그인 API")
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
        long youtubeScrapCnt = youtubeRecipeService.countYoutubeScrapByUser(user);
        long blogScrapCnt = blogRecipeService.countBlogScrapByUser(user);
        long recipeScrapCnt = recipeService.countRecipeScrapByUser(user);
        List<Recipe> userRecipes = recipeService.getRecipesByUser(user);
        UserDto.UserProfileResponse data = UserDto.UserProfileResponse.from(user, userRecipes, youtubeScrapCnt, blogScrapCnt, recipeScrapCnt);

        return success(data);
    }

    @ApiOperation(value = "카카오 로그인 API")
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
        long youtubeScrapCnt = youtubeRecipeService.countYoutubeScrapByUser(user);
        long blogScrapCnt = blogRecipeService.countBlogScrapByUser(user);
        long recipeScrapCnt = recipeService.countRecipeScrapByUser(user);
        List<Recipe> userRecipes = recipeService.getRecipesByUser(user);
        UserDto.UserProfileResponse data = UserDto.UserProfileResponse.from(user, userRecipes, youtubeScrapCnt, blogScrapCnt, recipeScrapCnt);

        return success(data);
    }

    @ApiOperation(value = "구글 로그인 API")
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
        long youtubeScrapCnt = youtubeRecipeService.countYoutubeScrapByUser(user);
        long blogScrapCnt = blogRecipeService.countBlogScrapByUser(user);
        long recipeScrapCnt = recipeService.countRecipeScrapByUser(user);
        List<Recipe> userRecipes = recipeService.getRecipesByUser(user);
        UserDto.UserProfileResponse data = UserDto.UserProfileResponse.from(user, userRecipes, youtubeScrapCnt, blogScrapCnt, recipeScrapCnt);

        return success(data);
    }

    @ApiOperation(value = "유저 프로필 조회 API")
    @ResponseBody
    @GetMapping("")
    public BaseResponse<UserDto.UserProfileResponse> getUser(@ApiIgnore final Authentication authentication) {

        if (authentication == null)
            throw new UserTokenNotExistException();

        User user = ((SecurityUser) authentication.getPrincipal()).getUser();
        long youtubeScrapCnt = youtubeRecipeService.countYoutubeScrapByUser(user);
        long blogScrapCnt = blogRecipeService.countBlogScrapByUser(user);
        long recipeScrapCnt = recipeService.countRecipeScrapByUser(user);
        List<Recipe> userRecipes = recipeService.getRecipesByUser(user);
        UserDto.UserProfileResponse data = UserDto.UserProfileResponse.from(user, userRecipes, youtubeScrapCnt, blogScrapCnt, recipeScrapCnt);

        return success(data);
    }

    @ApiOperation(value = "유저 프로필 수정 API")
    @ResponseBody
    @PatchMapping("")
    public BaseResponse<UserDto.UserProfileResponse> patchUser(@ApiIgnore final Authentication authentication,
                                                               @ApiParam(value = "수정할 회원 정보", required = true)
                                                               @RequestBody UserDto.UserProfileRequest request) {

        if (authentication == null)
            throw new UserTokenNotExistException();

        User user = ((SecurityUser) authentication.getPrincipal()).getUser();
        user = userService.updateUser(user, request);
        long youtubeScrapCnt = youtubeRecipeService.countYoutubeScrapByUser(user);
        long blogScrapCnt = blogRecipeService.countBlogScrapByUser(user);
        long recipeScrapCnt = recipeService.countRecipeScrapByUser(user);
        List<Recipe> userRecipes = recipeService.getRecipesByUser(user);
        UserDto.UserProfileResponse data = UserDto.UserProfileResponse.from(user, userRecipes, youtubeScrapCnt, blogScrapCnt, recipeScrapCnt);

        return success(data);
    }

    @ApiOperation(value = "회원 탈퇴 API")
    @ResponseBody
    @DeleteMapping("")
    public BaseResponse<Void> deleteUser(HttpServletRequest request, @ApiIgnore final Authentication authentication) {

        String jwt = jwtService.resolveToken(request);

        if (authentication == null || !StringUtils.hasText(jwt))
            throw new UserTokenNotExistException();

        User user = ((SecurityUser) authentication.getPrincipal()).getUser();

        userService.deleteUser(user, jwt);

        return success();
    }

    @ApiOperation(value = "로그아웃 API")
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
