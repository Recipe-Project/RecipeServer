package com.recipe.app.src.notice;

import com.recipe.app.src.notice.models.Notice;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface NoticeRepository extends CrudRepository<Notice, Integer> {
    Notice findFirstByActiveYn(String activeYn);
}
