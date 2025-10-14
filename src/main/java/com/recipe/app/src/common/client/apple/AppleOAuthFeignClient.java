package com.recipe.app.src.common.client.apple;

import com.recipe.app.src.common.client.apple.dto.ApplePublicKeysResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name = "apple-oauth-client", url = "https://appleid.apple.com/auth")
public interface AppleOAuthFeignClient {

    @GetMapping(value = "/keys")
    ApplePublicKeysResponse getPublicKeys();

}
