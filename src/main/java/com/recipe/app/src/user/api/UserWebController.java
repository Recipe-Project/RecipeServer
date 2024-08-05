package com.recipe.app.src.user.api;

import com.recipe.app.src.user.application.UserAuthClientService;
import com.recipe.app.src.user.application.UserService;
import com.recipe.app.src.user.application.dto.UserLoginRequest;
import com.recipe.app.src.user.application.dto.UserSocialLoginResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/users")
public class UserWebController {

    @Value("${kakao.client-id}")
    private String kakaoClientId;
    @Value("${kakao.redirect-uri}")
    private String kakaoRedirectURI;
    @Value("${naver.client-id}")
    private String naverClientId;
    @Value("${naver.redirect-uri}")
    private String naverRedirectURI;
    @Value("${google.client-id}")
    private String googleClientId;
    @Value("${google.redirect-uri}")
    private String googleRedirectURI;
    @Value("${withdrawal.uri}")
    private String withdrawalURI;

    private final UserService userService;
    private final UserAuthClientService userAuthClientService;

    public UserWebController(UserService userService, UserAuthClientService userAuthClientService) {
        this.userService = userService;
        this.userAuthClientService = userAuthClientService;
    }

    @GetMapping("/social-login")
    public String login(Model model) {

        model.addAttribute("kakaoClientId", kakaoClientId);
        model.addAttribute("kakaoRedirectURI", kakaoRedirectURI);
        model.addAttribute("naverClientId", naverClientId);
        model.addAttribute("naverRedirectURI", naverRedirectURI);
        model.addAttribute("googleClientId", googleClientId);
        model.addAttribute("googleRedirectURI", googleRedirectURI);

        return "/user-login";
    }

    @GetMapping("/withdrawal")
    public String withdraw(Model model) {

        return "/user-withdrawal-success";
    }

    @GetMapping("/auth/kakao/callback")
    public String kakaoLogin(String code, Model model) {

        UserLoginRequest request = userAuthClientService.getKakaoLoginRequest(code);

        UserSocialLoginResponse data = userService.kakaoLogin(request);

        model.addAttribute("accessToken", data.getAccessToken());
        model.addAttribute("withdrawalURI", withdrawalURI);

        return "/user-withdrawal";
    }

    @GetMapping("/auth/naver/callback")
    public String naverLogin(String code, String state, Model model) {

        UserLoginRequest request = userAuthClientService.getNaverLoginRequest(code, state);

        UserSocialLoginResponse data = userService.naverLogin(request);

        model.addAttribute("accessToken", data.getAccessToken());
        model.addAttribute("withdrawalURI", withdrawalURI);

        return "/user-withdrawal";
    }

    @GetMapping("/auth/google/callback")
    public String googleLogin(String code, Model model) {

        UserLoginRequest request = userAuthClientService.getGoogleLoginRequest(code);

        UserSocialLoginResponse data = userService.googleLogin(request);

        model.addAttribute("accessToken", data.getAccessToken());
        model.addAttribute("withdrawalURI", withdrawalURI);

        return "/user-withdrawal";
    }
}
