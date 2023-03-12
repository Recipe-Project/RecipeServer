package com.recipe.app.src.notice;

import com.recipe.app.common.response.BaseResponse;
import com.recipe.app.src.notice.models.GetNoticeRes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.recipe.app.common.response.BaseResponse.success;

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
        GetNoticeRes getNoticeRes = noticeProvider.retrieveNotice();
        return success(getNoticeRes);
    }
}
