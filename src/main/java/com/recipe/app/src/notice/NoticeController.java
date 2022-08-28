package com.recipe.app.src.notice;

import com.recipe.app.config.BaseException;
import com.recipe.app.config.BaseResponse;
import com.recipe.app.src.notice.models.GetNoticeRes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/notice")
public class NoticeController {
    private final NoticeProvider noticeProvider;

    @Autowired
    public NoticeController(NoticeProvider noticeProvider) {
        this.noticeProvider = noticeProvider;
    }

    /**
     * 공지 조회 API
     * [GET] /notice
     * @return BaseResponse<GetNoticeRes>
     */
    @GetMapping("")
    public BaseResponse<GetNoticeRes> getNotice() {
        try {
            GetNoticeRes getNoticeRes = noticeProvider.retrieveNotice();
            return new BaseResponse<>(getNoticeRes);
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }
}
