package com.recipe.app.src.user.models;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.*;

@NoArgsConstructor(access = AccessLevel.PUBLIC) // Unit Test 를 위해 PUBLIC
@Getter
public class PatchUserReq {
    private String profilePhoto;
    @Size(min=1)
    private String userName;
    private String email;
    private String phoneNumber;
}
