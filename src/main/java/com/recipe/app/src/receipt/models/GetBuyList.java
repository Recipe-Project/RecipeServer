package com.recipe.app.src.receipt.models;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class GetBuyList {
    private final Integer buyIdx;
    private final String buyName;
    private final Integer buyCnt;
    private final Integer buyPrice;
}
