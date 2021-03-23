package com.recipe.app.src.fridge;

import com.recipe.app.src.fridge.models.Fridge;
import com.recipe.app.src.user.models.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface FridgeRepository extends CrudRepository<Fridge, Integer> {
    List<Fridge> findByUserAndStatus(User user, String status);

}