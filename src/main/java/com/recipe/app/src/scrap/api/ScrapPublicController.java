package com.recipe.app.src.scrap.api;

import com.recipe.app.common.response.BaseResponse;
import com.recipe.app.src.scrap.application.ScrapPublicService;
import com.recipe.app.src.scrap.application.dto.ScrapPublicDto;
import com.recipe.app.src.user.domain.SecurityUser;
import com.recipe.app.src.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.stream.Collectors;

import static com.recipe.app.common.response.BaseResponse.success;

@RestController
@RequiredArgsConstructor
@RequestMapping("/scraps/recipe")
public class ScrapPublicController {

    private final ScrapPublicService scrapPublicService;

    @ResponseBody
    @PostMapping("")
    public BaseResponse<Void> postScrapRecipe(final Authentication authentication, @RequestBody ScrapPublicDto.ScrapPublicRequest request) {
        User user = ((SecurityUser) authentication.getPrincipal()).getUser();
        scrapPublicService.createOrDeleteScrapRecipe(request.getRecipeId(), user);

        return success();
    }

    @ResponseBody
    @GetMapping("")
    public BaseResponse<ScrapPublicDto.ScrapPublicsResponse> getScrapRecipes(final Authentication authentication) {
        User user = ((SecurityUser) authentication.getPrincipal()).getUser();
        ScrapPublicDto.ScrapPublicsResponse data = new ScrapPublicDto.ScrapPublicsResponse(scrapPublicService.countScrapPublicsByUser(user),
                scrapPublicService.retrieveScrapRecipes(user).stream()
                        .map((sp) -> new ScrapPublicDto.ScrapPublicResponse(sp, scrapPublicService.countScrapPublicsByRecipe(sp.getRecipeInfo())))
                        .collect(Collectors.toList()));

        return success(data);
    }

}
