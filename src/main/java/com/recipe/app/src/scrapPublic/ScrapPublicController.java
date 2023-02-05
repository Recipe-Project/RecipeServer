package com.recipe.app.src.scrapPublic;

import com.recipe.app.common.exception.BaseException;
import com.recipe.app.common.response.BaseResponse;
import com.recipe.app.src.scrapPublic.models.*;
import com.recipe.app.common.utils.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;



@RestController
@RequestMapping("/scraps/recipe")
public class ScrapPublicController {
    private final ScrapPublicProvider scrapPublicProvider;
    private final ScrapPublicService scrapPublicService;
    private final JwtService jwtService;

    @Autowired
    public ScrapPublicController(ScrapPublicProvider scrapPublicProvider, ScrapPublicService scrapPublicService , JwtService jwtService) {
        this.scrapPublicProvider = scrapPublicProvider;
        this.scrapPublicService = scrapPublicService;
        this.jwtService = jwtService;
    }

    /**
     * 레시피 스크랩하기 API
     * [POST] /scraps/recipe
     * @return BaseResponse<PostScrapPublicRes>
     * @RequestBody parameters
     */
    @ResponseBody
    @PostMapping("")
    public BaseResponse<PostScrapPublicRes> postScrapRecipe(@RequestBody PostScrapPublicReq parameters) throws BaseException {

        try {
            Integer userIdx = jwtService.getUserId();

            // 이미 발급 받은건지
            ScrapPublic scrapPublic = null;
            scrapPublic = scrapPublicProvider.retrieveScrapRecipe(parameters.getRecipeId(), userIdx);


                PostScrapPublicRes postScrapPublicRes;
                if (scrapPublic != null) {
                postScrapPublicRes =  scrapPublicService.deleteScrapRecipe(parameters.getRecipeId(),userIdx);
            }
            else{
                postScrapPublicRes = scrapPublicService.createScrapRecipe(parameters.getRecipeId(),userIdx);
            }
            return new BaseResponse<>(postScrapPublicRes);
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }


    }
    /**
     * 레시피 스크랩 조회 API
     * [GET] /scraps/recipe
     * @return BaseResponse<List<GetScrapRecipesRes>>
     */
    @ResponseBody
    @GetMapping("")
    public BaseResponse <GetScrapPublicsRes> getScrapRecipes() throws BaseException {


        try {
            Integer userIdx = jwtService.getUserId();

            GetScrapPublicsRes getScrapPublicsRes = scrapPublicProvider.retrieveScrapRecipes(userIdx);


            return new BaseResponse<>(getScrapPublicsRes);
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }
//    /**
//     * 레시피 스크랩 조회 API
//     * [GET] /scraps/recipe
//     * @return BaseResponse<List<GetScrapRecipesRes>>
//     */
//    @ResponseBody
//    @GetMapping("")
//    public BaseResponse <GetScrapPublicsRes> getScrapRecipes(@RequestParam(value = "sort") @Nullable Integer sort) throws BaseException {
//
//
//        try {
//            Integer userIdx = jwtService.getUserId();
//
//            GetScrapPublicsRes getScrapPublicsRes = scrapPublicProvider.retrieveScrapRecipes(userIdx,sort);
//
//
//            return new BaseResponse<>(getScrapPublicsRes);
//        } catch (BaseException exception) {
//            return new BaseResponse<>(exception.getStatus());
//        }
//    }



}
