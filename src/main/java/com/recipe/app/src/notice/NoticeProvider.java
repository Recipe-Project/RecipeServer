package com.recipe.app.src.notice;

import com.recipe.app.common.exception.BaseException;
import com.recipe.app.src.notice.models.GetNoticeRes;
import com.recipe.app.src.notice.models.Notice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.recipe.app.common.response.BaseResponseStatus.NOT_FOUND_NOTICE;

@Service
public class NoticeProvider {
    private final NoticeRepository noticeRepository;

    @Autowired
    public NoticeProvider(NoticeRepository noticeRepository) {
        this.noticeRepository = noticeRepository;
    }

    /**
     * 공지 조회 API
     * @param
     * @return GetNoticeRes
     * @throws BaseException
     */
    public GetNoticeRes retrieveNotice() throws BaseException {
       Notice notice = noticeRepository.findFirstByActiveYn("Y");
        if(notice == null)
            throw new BaseException(NOT_FOUND_NOTICE);
        return new GetNoticeRes(notice.getIdx(), notice.getTitle(), notice.getContent());
    }
}
