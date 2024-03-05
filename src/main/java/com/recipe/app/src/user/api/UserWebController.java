package com.recipe.app.src.user.api;

import com.recipe.app.config.BaseException;
import com.recipe.app.src.user.UserService;
import com.recipe.app.src.user.models.PostUserRes;
import com.recipe.app.src.user.models.User;
import com.recipe.app.utils.JwtService;
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

    public UserWebController(UserService userService, JwtService jwtService) {
        this.userService = userService;
        this.jwtService = jwtService;
    }

    @GetMapping("/social-login")
    public String login(Model model) {

        model.addAttribute("kakaoClientId", kakaoClientId);
        model.addAttribute("kakaoRedirectURI", kakaoRedirectURI);

        return "/user-login";
    }

    @GetMapping("/withdrawal")
    public String withdraw() {

        return "/user-withdrawal-success";
    }

    @GetMapping("/auth/kakao/callback")
    public String kakaoLogin(String code, Model model) throws IOException, ParseException, BaseException {

        String accessToken = userService.getKakaoAccessToken(code);

        PostUserRes postUserRes = userService.kakaoLogin(accessToken, null);

        model.addAttribute("jwtToken", jwtService.createJwt(postUserRes.getUserIdx()));

        return "/user-withdrawal";
    }
}
