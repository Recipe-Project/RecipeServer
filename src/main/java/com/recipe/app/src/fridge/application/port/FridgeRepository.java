package com.recipe.app.src.fridge.application.port;

import com.recipe.app.src.fridge.domain.Fridge;
import com.recipe.app.src.user.domain.User;

import java.util.List;
import java.util.Optional;

public interface FridgeRepository {
    List<Fridge> findByUser(User user);

    List<Fridge> saveAll(List<Fridge> fridges);

    Optional<Fridge> findByUserAndFridgeId(User user, Long fridgeId);

    void delete(Fridge fridge);

    void save(Fridge fridge);
}
