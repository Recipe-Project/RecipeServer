package com.recipe.app.src.fridge.mapper;

import com.recipe.app.src.fridge.domain.Fridge;
import com.recipe.app.src.user.domain.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface FridgeRepository extends CrudRepository<Fridge, Integer> {
    List<Fridge> findAllByUserAndStatus(User user, String status);

    List<Fridge> findAllByUserAndStatusAndIngredientNameIn(User user, String active, List<String> ingredientName);

    @Query(nativeQuery = true, value = "SELECT f.* FROM Fridge f WHERE status=:status AND  DATEDIFF(f.expiredAt, :today) = 3")
    List<Fridge> findAllByStatusAnd3DaysBeforeExpiredAt(String status, String today);
}