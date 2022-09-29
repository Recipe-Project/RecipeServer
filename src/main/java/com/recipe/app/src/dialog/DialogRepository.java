package com.recipe.app.src.dialog;

import com.recipe.app.src.dialog.models.Dialog;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DialogRepository extends CrudRepository<Dialog, Integer> {
    Dialog findFirstByActiveYn(String activeYn);
}
