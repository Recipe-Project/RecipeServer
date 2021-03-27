package com.recipe.app.src.receipt.models;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;


@NoArgsConstructor(access = AccessLevel.PUBLIC)
@Getter
public class PostReceiptReq {
    private String title;
    private String receiptDate;
    private List<PostBuyList> buyList;
}
