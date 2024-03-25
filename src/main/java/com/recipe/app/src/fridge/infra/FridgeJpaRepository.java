package com.recipe.app.src.fridge.infra;

import com.recipe.app.src.user.domain.User;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface FridgeJpaRepository extends CrudRepository<FridgeEntity, Long> {
    List<FridgeEntity> findByUser(User user);

    Optional<FridgeEntity> findByUserAndFridgeId(User user, Long fridgeId);
}