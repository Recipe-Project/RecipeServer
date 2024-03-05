package com.recipe.app.src.user.api;

import com.recipe.app.common.utils.JwtService;
import com.recipe.app.src.user.application.UserService;
import com.recipe.app.src.user.domain.User;
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
    private final JwtService jwtService;
    @Value("${kakao.client-id}")
    private String kakaoClientId;
    @Value("${kakao.redirect-uri}")
    private String kakaoRedirectURI;
    @Value("${naver.client-id}")
    private String naverClientId;
    @Value("${naver.redirect-uri}")
    private String naverRedirectURI;

    public UserWebController(UserService userService, JwtService jwtService) {
        this.userService = userService;
        this.jwtService = jwtService;
    }

    @GetMapping("/social-login")
    public String login(Model model) {

        model.addAttribute("kakaoClientId", kakaoClientId);
        model.addAttribute("kakaoRedirectURI", kakaoRedirectURI);
        model.addAttribute("naverClientId", naverClientId);
        model.addAttribute("naverRedirectURI", naverRedirectURI);

        return "/user-login";
    }

    @GetMapping("/withdrawal")
    public String withdraw() {

        return "/user-withdrawal-success";
    }

    @GetMapping("/auth/kakao/callback")
    public String kakaoLogin(String code, Model model) throws IOException, ParseException {

        String accessToken = userService.getKakaoAccessToken(code);

        User user = userService.kakaoLogin(accessToken, null);

        model.addAttribute("jwtToken", jwtService.createJwt(user.getUserId()));

        return "/user-withdrawal";
    }

    @GetMapping("/auth/naver/callback")
    public String naverLogin(String code, String state, Model model) throws IOException, ParseException {

        String accessToken = userService.getNaverAccessToken(code, state);

        User user = userService.naverLogin(accessToken, null);

        model.addAttribute("jwtToken", jwtService.createJwt(user.getUserId()));

        return "/user-withdrawal";
    }
}
