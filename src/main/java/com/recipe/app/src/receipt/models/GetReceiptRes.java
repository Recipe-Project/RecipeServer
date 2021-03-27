package com.recipe.app.src.receipt.models;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class GetReceiptRes {
    private final Integer receiptIdx;
    private final Integer userIdx;
    private final String title;
    private final String receiptDate;
    private final List<GetBuyList> buyList;
}
