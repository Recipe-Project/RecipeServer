package com.recipe.app.src.receipt;

import com.recipe.app.config.BaseException;
import com.recipe.app.src.buy.models.Buy;
import com.recipe.app.src.receipt.models.*;
import com.recipe.app.src.user.UserProvider;
import com.recipe.app.src.user.models.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

import static com.recipe.app.config.BaseResponseStatus.*;

@Service
public class ReceiptProvider {
    private final UserProvider userProvider;
    private final ReceiptRepository receiptRepository;

    @Autowired
    public ReceiptProvider(UserProvider userProvider, ReceiptRepository receiptRepository){
        this.userProvider = userProvider;
        this.receiptRepository = receiptRepository;
    }

    /**
     * 영수증 조회
     *
     * @param receiptIdx
     * @return Receipt
     * @throws BaseException
     */
    public Receipt retrieveReceiptByReceiptIdx(Integer receiptIdx) throws BaseException {
        Receipt receipt;
        try {
            receipt = receiptRepository.findById(receiptIdx).orElse(null);
        } catch (Exception ignored) {
            throw new BaseException(DATABASE_ERROR);
        }

        if (receipt == null || !receipt.getStatus().equals("ACTIVE")) {
            throw new BaseException(NOT_FOUND_RECEIPT);
        }

        return receipt;
    }

    /**
     * 영수증 전체 조회
     * @param jwtUserIdx
     * @return List<GetReceiptsRes>
     * @throws BaseException
     */
    public List<GetReceiptsRes> retrieveReceipts(int jwtUserIdx) throws BaseException {
        User user = userProvider.retrieveUserByUserIdx(jwtUserIdx);

        List<Receipt> receipts = new ArrayList<>();
        for(int i=0;i<user.getReceipts().size();i++){
            if(user.getReceipts().get(i).getStatus().equals("ACTIVE"))
                receipts.add(user.getReceipts().get(i));
        }

        return receipts.stream().map(receipt -> {
            Integer receiptIdx = receipt.getReceiptIdx();
            String title = receipt.getTitle();
            Date date = receipt.getReceiptDate();
            SimpleDateFormat datetime = new SimpleDateFormat("yyyy.M.d", Locale.KOREA);
            datetime.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));
            String receiptDate = datetime.format(date);
            List<GetBuyList> buyList = new ArrayList<>();
            List<Buy> buys = receipt.getBuys();
            for(int i=0;i<buys.size();i++) {
                Buy buy = buys.get(i);
                if(buy.getStatus().equals("ACTIVE")) {
                    Integer buyIdx = buy.getBuyIdx();
                    String buyName = buy.getBuyName();

                    buyList.add(new GetBuyList(buyIdx, buyName));
                }
            }

            return new GetReceiptsRes(receiptIdx, jwtUserIdx, title, receiptDate, buyList);
        }).collect(Collectors.toList());
    }

    /**
     * 영수증 상세 조회
     * @param jwtUserIdx, receiptIdx
     * @return GetReceiptRes
     * @throws BaseException
     */
    public GetReceiptRes retrieveReceipt(int jwtUserIdx, Integer receiptIdx) throws BaseException {
        Receipt receipt= retrieveReceiptByReceiptIdx(receiptIdx);

        String title = receipt.getTitle();
        Date date = receipt.getReceiptDate();
        SimpleDateFormat datetime = new SimpleDateFormat("yyyy.M.d", Locale.KOREA);
        datetime.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));
        String receiptDate = datetime.format(date);
        List<GetBuyList> buyList = new ArrayList<>();
        List<Buy> buys = receipt.getBuys();
        for(int i=0;i<buys.size();i++) {
            Buy buy = buys.get(i);
            if(buy.getStatus().equals("ACTIVE")) {
                Integer buyIdx = buy.getBuyIdx();
                String buyName = buy.getBuyName();

                buyList.add(new GetBuyList(buyIdx, buyName));
            }
        }

        return new GetReceiptRes(receiptIdx, jwtUserIdx, title, receiptDate, buyList);

    }
}
