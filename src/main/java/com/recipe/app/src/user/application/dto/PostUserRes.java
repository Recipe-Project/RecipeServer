package com.recipe.app.src.user.application.dto;

import lombok.*;

@Getter
@AllArgsConstructor
public class PostUserRes {
    private final Integer userIdx;
    private final String jwt;
}
