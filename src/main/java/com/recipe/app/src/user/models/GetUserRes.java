package com.recipe.app.src.user.models;

import lombok.*;

import java.util.List;

@Getter
@AllArgsConstructor
public class GetUserRes {
    private final Integer userIdx;
    private final String profilePhoto;
    private final String userName;
    private final Integer youtubeScrapCnt;
    private final Integer blogScrapCnt;
    private final Integer recipeScrapCnt;
    private final Integer myRecipeTotalSize;
    private final List<MypageMyRecipeList> myRecipeList;
}
