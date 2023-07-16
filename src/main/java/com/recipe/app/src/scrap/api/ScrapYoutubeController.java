package com.recipe.app.src.scrap.api;

import com.recipe.app.common.exception.BaseException;
import com.recipe.app.common.response.BaseResponse;
import com.recipe.app.src.scrap.application.ScrapYoutubeService;
import com.recipe.app.src.scrap.application.dto.ScrapYoutubeDto;
import com.recipe.app.src.scrapYoutube.models.GetScrapYoutubesRes;
import com.recipe.app.src.scrapYoutube.models.PostScrapYoutubeReq;
import com.recipe.app.src.scrapYoutube.models.PostScrapYoutubeRes;
import com.recipe.app.src.scrap.domain.ScrapYoutube;
import com.recipe.app.common.utils.JwtService;
import com.recipe.app.src.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.stream.Collectors;

import static com.recipe.app.common.response.BaseResponse.success;
import static com.recipe.app.common.response.BaseResponseStatus.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/scraps/youtube")
public class ScrapYoutubeController {

    private final ScrapYoutubeService scrapYoutubeService;

    @PostMapping("")
    public BaseResponse<Void> postScrapsYoutube(final Authentication authentication, @RequestBody ScrapYoutubeDto.ScrapYoutubeRequest request) {
        User user = ((User) authentication.getPrincipal());
        scrapYoutubeService.createOrDeleteScrapYoutube(request, user);

        return success();
    }

    @GetMapping("")
    public BaseResponse<ScrapYoutubeDto.ScrapYoutubesResponse> getScrapsYoutube(final Authentication authentication) {
        User user = ((User) authentication.getPrincipal());
        ScrapYoutubeDto.ScrapYoutubesResponse data = new ScrapYoutubeDto.ScrapYoutubesResponse(scrapYoutubeService.countScrapYoutubesByUser(user),
                scrapYoutubeService.retrieveScrapYoutubes(user).stream()
                        .map((sy) -> new ScrapYoutubeDto.ScrapYoutubeResponse(sy, scrapYoutubeService.countScrapYoutubesByYoutubeId(sy.getYoutubeId())))
                        .collect(Collectors.toList()));

        return success(data);
    }
//
//    /**
//     * 유튜브 스크랩 조회 API
//     * [GET] /scraps/youtube
//     * @return BaseResponse<List<GetScrapYoutubesRes>>
//     * @PageableDefault pageable
//     */
//    @GetMapping("")
//    public BaseResponse<GetScrapYoutubesRes> getScrapsYoutube(@RequestParam(value = "sort") @Nullable Integer sort) {
//
//        GetScrapYoutubesRes getScrapYoutubesRes = null;
//        try {
//            Integer userIdx = jwtService.getUserId();
//
//            getScrapYoutubesRes = scrapYoutubeProvider.retrieveScrapYoutubeList(userIdx,sort);
//
//            return new BaseResponse<>(getScrapYoutubesRes);
//
//        } catch (BaseException exception) {
//            return new BaseResponse<>(exception.getStatus());
//        }
//    }


}