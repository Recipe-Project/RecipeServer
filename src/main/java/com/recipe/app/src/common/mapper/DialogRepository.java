package com.recipe.app.src.common.mapper;

import com.recipe.app.src.common.domain.Dialog;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DialogRepository extends CrudRepository<Dialog, Integer> {
    Optional<Dialog> findFirstByActiveYn(String activeYn);
}
