package com.recipe.app.src.fridge.infra;

import com.recipe.app.src.user.infra.UserEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface FridgeJpaRepository extends CrudRepository<FridgeEntity, Long> {
    List<FridgeEntity> findByUser(UserEntity user);

    Optional<FridgeEntity> findByUserAndFridgeId(UserEntity user, Long fridgeId);
}