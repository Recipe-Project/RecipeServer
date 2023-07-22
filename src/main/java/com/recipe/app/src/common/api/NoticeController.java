package com.recipe.app.src.common.api;

import com.recipe.app.common.response.BaseResponse;
import com.recipe.app.src.common.application.dto.NoticeDto;
import com.recipe.app.src.common.application.NoticeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.recipe.app.common.response.BaseResponse.success;

@RestController
@RequiredArgsConstructor
@RequestMapping("/notice")
public class NoticeController {
    private final NoticeService noticeProvider;

    @GetMapping("")
    public BaseResponse<NoticeDto> getNotice() {
        NoticeDto data = new NoticeDto(noticeProvider.retrieveNotice());

        return success(data);
    }
}
