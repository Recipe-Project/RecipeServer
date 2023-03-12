package com.recipe.app.src.receipt;

import com.recipe.app.common.exception.BaseException;
import com.recipe.app.common.response.BaseResponse;
import com.recipe.app.src.receipt.models.*;
import com.recipe.app.common.utils.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import static com.recipe.app.common.response.BaseResponse.*;
import static com.recipe.app.common.response.BaseResponseStatus.*;

import java.util.List;


@RestController
@RequestMapping("/receipts")
public class ReceiptController {
    private final ReceiptProvider receiptProvider;
    private final ReceiptService receiptService;
    private final JwtService jwtService;

    @Autowired
    public ReceiptController(ReceiptProvider receiptProvider, ReceiptService receiptService, JwtService jwtService){
        this.receiptProvider = receiptProvider;
        this.receiptService = receiptService;
        this.jwtService = jwtService;
    }

    /**
     * 영수증 전체 조회 API
     * [GET] /receipts
     * @return BaseResponse<List<GetReceiptsRes>>
     */
    @ResponseBody
    @GetMapping("")
    public BaseResponse<List<GetReceiptsRes>> getReceipts() {
        int jwtUserIdx;
        jwtUserIdx = jwtService.getUserId();
        List<GetReceiptsRes> getReceiptsResList = receiptProvider.retrieveReceipts(jwtUserIdx);
        return success(getReceiptsResList);
    }

    /**
     * 영수증 상세 조회 API
     * [GET] /receipts/:receiptIdx
     * @return BaseResponse<GetReceiptRes>
     */
    @ResponseBody
    @GetMapping("/{receiptIdx}")
    public BaseResponse<GetReceiptRes> getReceipt(@PathVariable Integer receiptIdx) {
        int jwtUserIdx;
        jwtUserIdx = jwtService.getUserId();
        GetReceiptRes getReceiptRes = receiptProvider.retrieveReceipt(jwtUserIdx, receiptIdx);
        return success(getReceiptRes);
    }

    /**
     * 영수증 삭제 API
     * [DELETE] /receipts/:receiptIdx
     */
    @ResponseBody
    @DeleteMapping("/{receiptIdx}")
    public BaseResponse<Void> deleteReceipt(@PathVariable Integer receiptIdx) {
        if(receiptIdx==null || receiptIdx<=0){
            throw new BaseException(RECEIPTS_EMPTY_RECEIPT_IDX);
        }

        int jwtUserIdx;
        jwtUserIdx = jwtService.getUserId();
        receiptService.deleteReceipt(jwtUserIdx, receiptIdx);
        return success();
    }

    /**
     * 영수증 수정 API
     * [PATCH] /receipts/:receiptIdx
     * @return BaseResponse<Void>
     */
    @ResponseBody
    @PatchMapping("/{receiptIdx}")
    public BaseResponse<Void> patchReceipt(@PathVariable Integer receiptIdx, @RequestBody PatchReceiptReq parameters) {
        if(receiptIdx==null || receiptIdx<=0){
            throw new BaseException(RECEIPTS_EMPTY_RECEIPT_IDX);
        }

        int jwtUserIdx;
        jwtUserIdx = jwtService.getUserId();
        receiptService.updateReceipt(jwtUserIdx, receiptIdx, parameters);
        return success();
    }

    /**
     * 영수증 입력 API
     * [PATCH] /receipts
     * @return BaseResponse<Void>
     */
    @ResponseBody
    @PostMapping("")
    public BaseResponse<Void> postReceipt(@RequestBody PostReceiptReq parameters) {
        int jwtUserIdx;
        jwtUserIdx = jwtService.getUserId();
        receiptService.createReceipt(jwtUserIdx, parameters);
        return success();
    }

    /**
     * 영수증으로 재료 입력 API
     * [POST] /receipts/ingredient
     * @return BaseResponse<List<PostReceiptIngredientRes>>
     */
    @ResponseBody
    @PostMapping("/ingredient")
    public BaseResponse<List<PostReceiptIngredientRes>> postReceiptIngredient(@RequestBody PostReceiptIngredientReq parameters) {
        int jwtUserIdx;
        jwtUserIdx = jwtService.getUserId();
        List<PostReceiptIngredientRes> postReceiptIngredientRes = receiptProvider.createReceiptIngredient(jwtUserIdx, parameters);
        return success(postReceiptIngredientRes);
    }
}
