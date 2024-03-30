package com.recipe.app.src.common.infra;

import com.recipe.app.src.common.domain.Dialog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DialogRepository extends JpaRepository<Dialog, Integer> {
    Optional<Dialog> findFirstByActiveYn(String activeYn);
}
