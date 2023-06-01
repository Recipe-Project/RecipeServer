package com.recipe.app.src.fridge;

import com.recipe.app.src.fridge.models.Fridge;
import com.recipe.app.src.ingredientCategory.models.IngredientCategory;
import com.recipe.app.src.user.domain.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface FridgeRepository extends CrudRepository<Fridge, Integer> {
    List<Fridge> findByUserAndStatus(User user, String status);

    List<Fridge> findByUserAndIngredientCategoryAndStatus(User user, IngredientCategory ingredientCategory, String status);

    Fridge findByUserAndIngredientNameAndStatus(User user, String ingredientName, String status);

    List<Fridge> findByStatus(String active);

    List<Fridge> findAllByUserAndStatusAndIngredientNameIn(User user, String active, List<String> ingredientName);

    void deleteAllByUserAndStatusAndIngredientNameIn(User user, String status, List<String> ingredientName);

    @Query(nativeQuery = true, value = "SELECT f.* FROM Fridge f WHERE status=:status AND  DATEDIFF(f.expiredAt, :today) = 3")
    List<Fridge> findAllByStatusAnd3DaysBeforeExpiredAt(String status, String today);
}