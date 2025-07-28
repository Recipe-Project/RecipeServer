package com.recipe.app.src.notice.infra;

import com.recipe.app.src.notice.domain.Notice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface NoticeRepository extends JpaRepository<Notice, Long> {

    Optional<Notice> findFirstByActiveYn(String activeYn);
}
