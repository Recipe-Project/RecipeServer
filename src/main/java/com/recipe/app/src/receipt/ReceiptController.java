package com.recipe.app.src.receipt;

import com.recipe.app.config.BaseException;
import com.recipe.app.config.BaseResponse;
import com.recipe.app.src.receipt.models.*;
import com.recipe.app.utils.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import static com.recipe.app.config.BaseResponseStatus.*;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.context.request.*;



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


}
