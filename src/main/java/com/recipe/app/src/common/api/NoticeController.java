package com.recipe.app.src.common.api;

import com.recipe.app.common.response.BaseResponse;
import com.recipe.app.src.common.application.dto.NoticeResponse;
import com.recipe.app.src.common.application.NoticeService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.recipe.app.common.response.BaseResponse.success;

@RestController
@RequestMapping("/notice")
public class NoticeController {
    private final NoticeService noticeService;

    public NoticeController(NoticeService noticeService) {
        this.noticeService = noticeService;
    }

    @GetMapping
    public BaseResponse<NoticeResponse> getNotice() {

        return success(noticeService.findNotice());
    }
}
