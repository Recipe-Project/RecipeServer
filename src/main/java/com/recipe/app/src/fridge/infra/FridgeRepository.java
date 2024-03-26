package com.recipe.app.src.fridge.infra;

import com.recipe.app.src.fridge.domain.Fridge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FridgeRepository extends JpaRepository<Fridge, Long> {
    List<Fridge> findByUserId(Long userId);

    Optional<Fridge> findByUserIdAndFridgeId(Long userId, Long fridgeId);
}