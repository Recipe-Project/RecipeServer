package com.recipe.app.src.user.models;

import lombok.*;

@Getter
@AllArgsConstructor
public class PostUserRes {
    private final Integer userIdx;
    private final String jwt;
}
