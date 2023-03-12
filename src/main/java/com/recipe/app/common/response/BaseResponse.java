package com.recipe.app.common.response;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import static com.recipe.app.common.response.BaseResponseStatus.SUCCESS;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BaseResponse<T> {
    private boolean success;
    private String message;
    private int code;
    private T result;

    public static <T> BaseResponse<T> success(T result) {
        return new BaseResponse<>(true, SUCCESS, result);
    }

    public static BaseResponse<?> error(BaseResponseStatus status) {
        return new BaseResponse<>(false, status, null);
    }

    public BaseResponse(boolean success, BaseResponseStatus status, T result) {
        this(success, status.getMessage(), status.getCode(), result);
    }

    public BaseResponse(boolean success, String message, int code, T result) {
        this.success = success;
        this.message = message;
        this.code = code;
        this.result = result;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public int getCode() {
        return code;
    }

    public T getResult() {
        return result;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("isSuccess", success)
                .append("message", message)
                .append("code", code)
                .append("result", result)
                .toString();
    }

    /*
    // 냉장고 채우기 API - 존재하는 재료가 있을 때 그 재료를 validation message에 추가
    public BaseResponse(BaseResponseStatus status,String ingredientName) {
        this.isSuccess = status.isSuccess();
        this.message = status.getMessage();
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

     */
}
