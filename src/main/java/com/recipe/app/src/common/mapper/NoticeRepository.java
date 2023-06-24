package com.recipe.app.src.common.mapper;

import com.recipe.app.src.common.domain.Notice;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface NoticeRepository extends CrudRepository<Notice, Integer> {
    Optional<Notice> findFirstByActiveYn(String activeYn);
}
