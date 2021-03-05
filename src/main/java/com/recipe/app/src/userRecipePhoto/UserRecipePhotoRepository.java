package com.recipe.app.src.userRecipePhoto;

import com.recipe.app.src.userRecipePhoto.models.UserRecipePhoto;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface UserRecipePhotoRepository extends CrudRepository<UserRecipePhoto, Integer> {
    List<UserRecipePhoto> findByUserRecipeIdxAndStatus(Integer userRecipeIdx, String status);
}
