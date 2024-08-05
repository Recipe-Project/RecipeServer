package com.recipe.app.src.etc.infra;

import com.recipe.app.src.etc.domain.Notice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface NoticeRepository extends JpaRepository<Notice, Long> {

    Optional<Notice> findFirstByActiveYn(String activeYn);
}
