package com.recipe.app.src.common.client.apple.dto;

import lombok.Getter;

@Getter
public class ApplePublicKeyResponse {

    private String kty;
    private String kid;
    private String use;
    private String alg;
    private String n;
    private String e;
}
