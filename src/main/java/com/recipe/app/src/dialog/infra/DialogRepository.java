package com.recipe.app.src.dialog.infra;

import com.recipe.app.src.dialog.domain.Dialog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DialogRepository extends JpaRepository<Dialog, Long> {
    Optional<Dialog> findFirstByActiveYn(String activeYn);
}
