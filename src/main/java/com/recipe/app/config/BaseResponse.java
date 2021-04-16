package com.recipe.app.config;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Getter;

import static com.recipe.app.config.BaseResponseStatus.POST_FRIDGES_EXIST_INGREDIENT_NAME;
import static com.recipe.app.config.BaseResponseStatus.POST_FRIDGES_BASKET_EXIST_INGREDIENT_NAME;
import static com.recipe.app.config.BaseResponseStatus.POST_FRIDGES_DIRECT_BASKET_DUPLICATED_INGREDIENT_NAME_IN_INGREDIENTS;
import static com.recipe.app.config.BaseResponseStatus.SUCCESS;

@Getter
@AllArgsConstructor
@JsonPropertyOrder({"isSuccess", "code", "message", "result"})
public class BaseResponse<T> {
    @JsonProperty("isSuccess")
    private final Boolean isSuccess;
//    private final String message;
    private String message;
    private final int code;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private T result;

    // 요청에 성공한 경우
    public BaseResponse(T result) {
        this.isSuccess = SUCCESS.isSuccess();
        this.message = SUCCESS.getMessage();
        this.code = SUCCESS.getCode();
        this.result = result;
    }

    // 요청에 실패한 경우
    public BaseResponse(BaseResponseStatus status) {
        this.isSuccess = status.isSuccess();
        this.message = status.getMessage();
        this.code = status.getCode();
    }

    // 냉장고 채우기 API - 존재하는 재료가 있을 때 그 재료를 validation message에 추가
    public BaseResponse(BaseResponseStatus status,String ingredientName) {
        this.isSuccess = status.isSuccess();
        if (status == POST_FRIDGES_EXIST_INGREDIENT_NAME){
            this.message = status.getMessage()+"냉장고에 이미 있는 재료("+ingredientName+")입니다.";
        }
        else if(status == POST_FRIDGES_BASKET_EXIST_INGREDIENT_NAME) {
            this.message =status.getMessage()+"냉장고 바구니에 이미 담긴 재료("+ingredientName+")가 있습니다. "+ingredientName+"을/를 삭제해주세요";
        }
        else if(status == POST_FRIDGES_DIRECT_BASKET_DUPLICATED_INGREDIENT_NAME_IN_INGREDIENTS){
            this.message =status.getMessage()+"기본으로 제공되는 재료("+ingredientName+")입니다. 재료에서 선택해주세요.";
        }

        this.code = status.getCode();
    }
}
