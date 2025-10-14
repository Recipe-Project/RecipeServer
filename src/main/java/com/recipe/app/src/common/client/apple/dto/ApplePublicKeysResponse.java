package com.recipe.app.src.common.client.apple.dto;

import lombok.Getter;

import java.util.List;

@Getter
public class ApplePublicKeysResponse {

    private List<ApplePublicKeyResponse> keys;

    public ApplePublicKeyResponse getMatchKey(String alg, String kid) {

        return this.keys
                .stream()
                .filter(key -> key.getAlg().equals(alg) && key.getKid().equals(kid))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Apple 로그인 시 필요한 key 값이 존재하지 않습니다."));
    }
}
