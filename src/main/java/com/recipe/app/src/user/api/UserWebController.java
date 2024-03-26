package com.recipe.app.src.user.api;

import com.recipe.app.src.user.application.UserService;
import com.recipe.app.src.user.application.dto.UserDto;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;

@Controller
@RequestMapping("/users")
public class UserWebController {

    private final UserService userService;
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

    public UserWebController(UserService userService) {
        this.userService = userService;
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
    public String withdraw() {

        return "/user-withdrawal-success";
    }

    @GetMapping("/auth/kakao/callback")
    public String kakaoLogin(String code, Model model) throws IOException, ParseException {

        String accessToken = userService.getKakaoAccessToken(code);

        UserDto.UserSocialLoginResponse data = userService.kakaoLogin(UserDto.UserLoginRequest.builder()
                .accessToken(accessToken)
                .build());

        model.addAttribute("jwtToken", data.getJwt());

        return "/user-withdrawal";
    }

    @GetMapping("/auth/naver/callback")
    public String naverLogin(String code, String state, Model model) throws IOException, ParseException {

        String accessToken = userService.getNaverAccessToken(code, state);

        UserDto.UserSocialLoginResponse data = userService.naverLogin(UserDto.UserLoginRequest.builder()
                .accessToken(accessToken)
                .build());

        model.addAttribute("jwtToken", data.getJwt());

        return "/user-withdrawal";
    }

    @GetMapping("/auth/google/callback")
    public String googleLogin(String code, Model model) throws IOException, ParseException {

        String idToken = userService.getGoogleIdToken(code);

        UserDto.UserSocialLoginResponse data = userService.googleLogin(UserDto.UserLoginRequest.builder()
                .accessToken(idToken)
                .build());

        model.addAttribute("jwtToken", data.getJwt());

        return "/user-withdrawal";
    }
}
