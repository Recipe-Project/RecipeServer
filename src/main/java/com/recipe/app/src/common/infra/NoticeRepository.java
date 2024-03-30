package com.recipe.app.src.common.infra;

import com.recipe.app.src.common.domain.Notice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface NoticeRepository extends JpaRepository<Notice, Integer> {

    Optional<Notice> findFirstByActiveYn(String activeYn);
}
