package com.recipe.app.src.common.application;

import com.recipe.app.src.common.domain.Notice;
import com.recipe.app.src.common.exception.NotFoundNoticeException;
import com.recipe.app.src.common.infra.NoticeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NoticeService {

    private final NoticeRepository noticeRepository;

    public Notice retrieveNotice() {
        return noticeRepository.findFirstByActiveYn("Y").orElseThrow(NotFoundNoticeException::new);
    }
}
