package com.recipe.app.src.userRecipe;

import com.recipe.app.config.BaseException;
import com.recipe.app.config.BaseResponse;
import com.recipe.app.src.userRecipe.models.*;
import com.recipe.app.utils.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.recipe.app.config.BaseResponseStatus.*;


@RestController
@RequestMapping("/my-recipes")
public class UserRecipeController {
    private final UserRecipeProvider userRecipeProvider;
    private final UserRecipeService userRecipeService;
    private final JwtService jwtService;

    @Autowired
    public UserRecipeController(UserRecipeProvider userRecipeProvider, UserRecipeService userRecipeService, JwtService jwtService) {
        this.userRecipeProvider = userRecipeProvider;
        this.userRecipeService = userRecipeService;
        this.jwtService = jwtService;
    }

    /**
     * 나만의 레시피 전체조회 API
     * [GET] /my-recipes
     * @return BaseResponse<List<GetMyRecipesRes>>
     */
    @GetMapping("")
    public BaseResponse<List<GetMyRecipesRes>> getMyRecipes(@PageableDefault(size=10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {

        try {
            Integer userIdx = jwtService.getUserId();
            List<GetMyRecipesRes> GetMyRecipesResList = userRecipeProvider.retrieveMyRecipesList(userIdx,pageable);

            return new BaseResponse<>(GetMyRecipesResList);

        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }

    /**
     * 나만의 레시피 상세조회 API
     * [GET] /my-recipes/:myRecipeIdx
     * @return BaseResponse<GetMyRecipeRes>
     */
    @GetMapping("/{myRecipeIdx}")
    public BaseResponse<GetMyRecipeRes> getMyRecipe(@PathVariable Integer myRecipeIdx) throws BaseException {
        Boolean existMyRecipe = userRecipeProvider.existMyRecipe(myRecipeIdx);
        if (!existMyRecipe){
            return new BaseResponse<>(NO_FOUND_MY_RECIPE);
        }

        try {
            Integer userIdx = jwtService.getUserId();
            GetMyRecipeRes getMyRecipeRes = userRecipeProvider.retrieveMyRecipe(userIdx,myRecipeIdx);
            return new BaseResponse<>(getMyRecipeRes);
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }

    /**
     * 나만의 레시피 삭제 API
     * [DELETE] /my-recipes/:myRecipeIdx
     * @return BaseResponse<GetMyRecipeRes>
     */
    @DeleteMapping("/{myRecipeIdx}")
    public BaseResponse<Void> deleteMyRecipe(@PathVariable Integer myRecipeIdx) {
        if (myRecipeIdx == null || myRecipeIdx <= 0) {
            return new BaseResponse<>(EMPTY_MY_RECIPEIDX);
        }

        try {
            Integer userIdx = jwtService.getUserId();
            userRecipeService.deleteUserRecipe(userIdx,myRecipeIdx);
            return new BaseResponse<>(SUCCESS);
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }

    }

    /**
     * 나만의 레시피 생성 API
     * [POST] /my-recipes
     * @return BaseResponse<PostMyRecipeRes>
     * @RequestBody PostMyRecipeReq parameters
     */
    @ResponseBody
    @PostMapping("")
    public BaseResponse<PostMyRecipeRes> postMyRecipe(@RequestBody PostMyRecipeReq parameters) {
        try {
            Integer userIdx = jwtService.getUserId();

            if (parameters.getPhotoUrlList() != null && parameters.getThumbnail() ==null) {
                return new BaseResponse<>(EMPTY_THUMBNAIL);
            }
            if (parameters.getPhotoUrlList() == null && parameters.getThumbnail() !=null) {
                return new BaseResponse<>(EMPTY_PHOTO_URL_LIST);
            }



            if (parameters.getTitle() == null) {
                return new BaseResponse<>(EMPTY_TITLE);
            }
            if (parameters.getContent() == null) {
                return new BaseResponse<>(EMPTY_CONTENT);
            }

            PostMyRecipeRes postMyRecipeRes = userRecipeService.createMyRecipe(parameters,userIdx);
            return new BaseResponse<>(postMyRecipeRes);
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }


    }

    /**
     * 나만의 레시피 수정 API
     * [PATCH] /my-recipes/:myRecipeIdx
     * @return BaseResponse<PatchMyRecipeRes>
     * @PathVariable myRecipeIdx
     * @RequestBody PostMyRecipeReq parameters
     */
    @ResponseBody
    @PatchMapping("{myRecipeIdx}")
    public BaseResponse<PatchMyRecipeRes> patchMyRecipe(@PathVariable Integer myRecipeIdx,@RequestBody PatchMyRecipeReq parameters) {


        try {
            Integer userIdx = jwtService.getUserId();
            if (myRecipeIdx == null || myRecipeIdx <= 0) {
                return new BaseResponse<>(EMPTY_MY_RECIPEIDX);
            }

            if (parameters.getPhotoUrlList() != null && parameters.getThumbnail() ==null) {
                return new BaseResponse<>(EMPTY_THUMBNAIL);
            }
            if (parameters.getPhotoUrlList() == null && parameters.getThumbnail() !=null) {
                return new BaseResponse<>(EMPTY_PHOTO_URL_LIST);
            }


            PatchMyRecipeRes patchMyRecipeRes = userRecipeService.updateMyRecipe(parameters,userIdx,myRecipeIdx);
            return new BaseResponse<>(patchMyRecipeRes);
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }


    }


}