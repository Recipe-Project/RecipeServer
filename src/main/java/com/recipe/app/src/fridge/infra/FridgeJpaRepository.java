package com.recipe.app.src.fridge.infra;

import com.recipe.app.src.user.infra.UserEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface FridgeJpaRepository extends CrudRepository<FridgeEntity, Long> {
    List<FridgeEntity> findByUser(UserEntity user);

    List<FridgeEntity> saveAll(List<FridgeEntity> fridgeEntities);

    Optional<FridgeEntity> findByUserAndFridgeId(UserEntity user, Long fridgeId);

    List<FridgeEntity> findAllByUserAndStatusAndIngredientNameIn(UserEntity user, String active, List<String> ingredientName);

    @Query(nativeQuery = true, value = "SELECT f.* FROM Fridge f WHERE status=:status AND  DATEDIFF(f.expiredAt, :today) = 3")
    List<FridgeEntity> findAllByStatusAnd3DaysBeforeExpiredAt(String status, String today);
}