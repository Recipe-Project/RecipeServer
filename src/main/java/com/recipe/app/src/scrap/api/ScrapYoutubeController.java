package com.recipe.app.src.scrap.api;

import com.recipe.app.common.response.BaseResponse;
import com.recipe.app.src.scrap.application.ScrapYoutubeService;
import com.recipe.app.src.scrap.application.dto.ScrapYoutubeDto;
import com.recipe.app.src.user.domain.SecurityUser;
import com.recipe.app.src.user.domain.User;
import com.recipe.app.src.user.exception.UserTokenNotExistException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.stream.Collectors;

import static com.recipe.app.common.response.BaseResponse.success;

@RestController
@RequiredArgsConstructor
@RequestMapping("/scraps/youtube")
public class ScrapYoutubeController {

    private final ScrapYoutubeService scrapYoutubeService;

    @PostMapping("")
    public BaseResponse<Void> postScrapsYoutube(final Authentication authentication, @RequestBody ScrapYoutubeDto.ScrapYoutubeRequest request) {
        if (authentication == null)
            throw new UserTokenNotExistException();

        User user = ((SecurityUser) authentication.getPrincipal()).getUser();
        scrapYoutubeService.createOrDeleteScrapYoutube(request, user);

        return success();
    }

    @GetMapping("")
    public BaseResponse<ScrapYoutubeDto.ScrapYoutubesResponse> getScrapsYoutube(final Authentication authentication) {
        if (authentication == null)
            throw new UserTokenNotExistException();

        User user = ((SecurityUser) authentication.getPrincipal()).getUser();
        ScrapYoutubeDto.ScrapYoutubesResponse data = new ScrapYoutubeDto.ScrapYoutubesResponse(scrapYoutubeService.countScrapYoutubesByUser(user),
                scrapYoutubeService.retrieveScrapYoutubes(user).stream()
                        .map((sy) -> new ScrapYoutubeDto.ScrapYoutubeResponse(sy, scrapYoutubeService.countScrapYoutubesByYoutubeId(sy.getYoutubeId())))
                        .collect(Collectors.toList()));

        return success(data);
    }
}