package com.recipe.app.src.user.models;

import lombok.*;

@Getter
@AllArgsConstructor
public class PatchUserRes {
    private final Integer userIdx;
    private final String socialId;
    private final String profilePhoto;
    private final String userName;
}
