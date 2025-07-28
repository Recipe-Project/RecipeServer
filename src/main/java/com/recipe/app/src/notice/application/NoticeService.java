package com.recipe.app.src.notice.application;

import com.recipe.app.src.notice.application.dto.NoticeResponse;
import com.recipe.app.src.notice.exception.NotFoundNoticeException;
import com.recipe.app.src.notice.infra.NoticeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class NoticeService {

    private final NoticeRepository noticeRepository;

    public NoticeService(NoticeRepository noticeRepository) {
        this.noticeRepository = noticeRepository;
    }

    @Transactional(readOnly = true)
    public NoticeResponse findNotice() {

        return NoticeResponse.from(noticeRepository.findFirstByActiveYn("Y")
                .orElseThrow(NotFoundNoticeException::new));
    }
}
