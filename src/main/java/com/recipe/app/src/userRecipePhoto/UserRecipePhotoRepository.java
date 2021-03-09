package com.recipe.app.src.userRecipePhoto;

import com.recipe.app.src.userRecipePhoto.models.UserRecipePhoto;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository // => JPA => Hibernate => ORM => Database 객체지향으로 접근하게 해주는 도구이다
public interface UserRecipePhotoRepository extends CrudRepository<UserRecipePhoto, Integer> {
    List<UserRecipePhoto> findByUserRecipeIdxAndStatus(Integer userRecipeIdx, String status);
}
