package com.recipe.app.src.user.application.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UserWithdrawRequest {

    private String withdrawalReason;
}
