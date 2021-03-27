package com.recipe.app.src.receipt;

import com.recipe.app.config.BaseException;
import com.recipe.app.src.buy.BuyRepository;
import com.recipe.app.src.buy.models.Buy;
import com.recipe.app.src.fridge.FridgeRepository;
import com.recipe.app.src.fridge.models.Fridge;
import com.recipe.app.src.ingredientCategory.IngredientCategoryProvider;
import com.recipe.app.src.ingredientCategory.models.IngredientCategory;
import com.recipe.app.src.receipt.models.*;
import com.recipe.app.src.user.UserProvider;
import com.recipe.app.src.user.models.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import static com.recipe.app.config.BaseResponseStatus.*;

@Service
public class ReceiptService {
    private final BuyRepository buyRepository;
    private final IngredientCategoryProvider ingredientCategoryProvider;
    private final FridgeRepository fridgeRepository;
    private final UserProvider userProvider;
    private final ReceiptProvider receiptProvider;
    private final ReceiptRepository receiptRepository;

    @Autowired
    public ReceiptService(BuyRepository buyRepository, IngredientCategoryProvider ingredientCategoryProvider, FridgeRepository fridgeRepository, UserProvider userProvider, ReceiptProvider receiptProvider, ReceiptRepository receiptRepository){
        this.buyRepository = buyRepository;
        this.ingredientCategoryProvider = ingredientCategoryProvider;
        this.fridgeRepository = fridgeRepository;
        this.userProvider = userProvider;
        this.receiptProvider = receiptProvider;
        this.receiptRepository = receiptRepository;
    }

    /**
     * 영수증 삭제 API
     * @param jwtUserIdx, receiptIdx
     * @throws BaseException
     */
    @Transactional(rollbackFor = Exception.class)
    public void deleteReceipt(Integer jwtUserIdx, Integer receiptIdx) throws BaseException {
        Receipt receipt = receiptProvider.retrieveReceiptByReceiptIdx(receiptIdx);

        if(receipt.getUser().getUserIdx()!=jwtUserIdx)
            throw new BaseException(FORBIDDEN_ACCESS);

        receipt.setStatus("INACTIVE");
        try {
            receipt = receiptRepository.save(receipt);
        } catch (Exception ignored) {
            throw new BaseException(DATABASE_ERROR);
        }

        List<Buy> buys = receipt.getBuys();
        for(int i=0;i<buys.size();i++){
            buys.get(i).setStatus("INACTIVE");

            try {
                buyRepository.save(buys.get(i));
            } catch (Exception exception) {
                throw new BaseException(DATABASE_ERROR);
            }
        }
    }

    /**
     * 영수증 수정
     * @param patchReceiptReq
     * @return void
     * @throws BaseException
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateReceipt(Integer jwtUserIdx, Integer receiptIdx, PatchReceiptReq patchReceiptReq) throws BaseException {
        Receipt receipt = receiptProvider.retrieveReceiptByReceiptIdx(receiptIdx);
        //jwt 확인
        if(receipt.getUser().getUserIdx() != jwtUserIdx){
            throw new BaseException(FORBIDDEN_USER);
        }

        receipt.setTitle(patchReceiptReq.getTitle());
        String receiptDate = patchReceiptReq.getReceiptDate();
        SimpleDateFormat datetime = new SimpleDateFormat("yyyy.M.d", Locale.KOREA);
        datetime.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));
        Date date;
        try {
            date = datetime.parse(receiptDate);
        }catch (Exception e){
            throw new BaseException(DATE_PARSE_ERROR);
        }
        receipt.setReceiptDate(date);
        try {
            receiptRepository.save(receipt);
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }

        List<PatchBuyList> buyList = patchReceiptReq.getBuyList();
        for(int i=0;i< buyList.size();i++){
            PatchBuyList list = buyList.get(i);
            Buy buy;
            try {
                buy = buyRepository.findById(list.getBuyIdx()).orElse(null);
            } catch (Exception exception) {
                throw new BaseException(DATABASE_ERROR);
            }

            if(buy==null || buy.getReceipt().getReceiptIdx()!=receiptIdx){
                throw new BaseException(NOT_FOUND_BUY);
            }
            buy.setBuyName(list.getBuyName());
            buy.setBuyCnt(list.getBuyCnt());
            buy.setBuyPrice(list.getBuyPrice());

            try {
                buy = buyRepository.save(buy);
            } catch (Exception exception) {
                throw new BaseException(DATABASE_ERROR);
            }
        }
    }

    /**
     * 영수증 입력
     * @param postReceiptReq
     * @return void
     * @throws BaseException
     */
    @Transactional(rollbackFor = Exception.class)
    public void createReceipt(Integer jwtUserIdx, PostReceiptReq postReceiptReq) throws BaseException {
        User user = userProvider.retrieveUserByUserIdx(jwtUserIdx);

        //유저 정보 수정
        String title = postReceiptReq.getTitle();
        String receiptDate = postReceiptReq.getReceiptDate();
        SimpleDateFormat datetime = new SimpleDateFormat("yyyy.M.d", Locale.KOREA);
        datetime.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));
        Date date;
        try {
            date = datetime.parse(receiptDate);
        }catch (Exception e){
            throw new BaseException(DATE_PARSE_ERROR);
        }

        Receipt receipt = new Receipt(user, title, date);

        try {
            receipt = receiptRepository.save(receipt);
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }

        List<PostBuyList> postBuyList = postReceiptReq.getBuyList();
        for(int i = 0; i< postBuyList.size(); i++){
            PostBuyList buys = postBuyList.get(i);
            String buyName = buys.getBuyName();
            Integer buyCnt = buys.getBuyCnt();
            Integer buyPrice = buys.getBuyPrice();

            Buy buy = new Buy(receipt, buyName, buyCnt, buyPrice);

            try {
                buy = buyRepository.save(buy);
            } catch (Exception exception) {
                throw new BaseException(DATABASE_ERROR);
            }
        }
    }


    /**
     * 영수증으로 재료 입력
     * @param postReceiptIngredientReq
     * @return void
     * @throws BaseException
     */
    public void createReceiptIngredient(Integer jwtUserIdx, PostReceiptIngredientReq postReceiptIngredientReq) throws BaseException {
        User user = userProvider.retrieveUserByUserIdx(jwtUserIdx);

        List<ReceiptIngredientList> receiptIngredientList = postReceiptIngredientReq.getReceiptIngredientList();

        for(int i=0;i<receiptIngredientList.size();i++) {
            //유저 정보 수정
            ReceiptIngredientList ingredientList = receiptIngredientList.get(i);
            String ingredientName = ingredientList.getIngredientName();
            String ingredientIcon = ingredientList.getIngredientIcon();
            Integer ingredientCategoryIdx = ingredientList.getIngredientCategoryIdx();
            String storageMethod = ingredientList.getStorageMethod();
            String expiredAt = ingredientList.getExpiredAt();
            Integer count = ingredientList.getCount();
            SimpleDateFormat datetime = new SimpleDateFormat("yyyy.M.d", Locale.KOREA);
            datetime.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));
            Date date;
            try {
                date = datetime.parse(expiredAt);
            } catch (Exception e) {
                throw new BaseException(DATE_PARSE_ERROR);
            }
            IngredientCategory ingredientCategory = ingredientCategoryProvider.retrieveIngredientCategoryByIngredientCategoryIdx(ingredientCategoryIdx);

            Fridge fridge = new Fridge(user, ingredientName, ingredientIcon, ingredientCategory, storageMethod, date, count);

            try {
                fridge = fridgeRepository.save(fridge);
            } catch (Exception exception) {
                throw new BaseException(DATABASE_ERROR);
            }
        }
    }
}
