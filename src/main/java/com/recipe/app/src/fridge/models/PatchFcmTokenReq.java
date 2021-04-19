package com.recipe.app.src.fridge.models;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;



@Getter
@NoArgsConstructor(access = AccessLevel.PUBLIC)
public class PatchFcmTokenReq {
    private String fcmToken;
}