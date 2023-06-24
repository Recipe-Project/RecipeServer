package com.recipe.app.src.user.mapper;

import com.recipe.app.src.user.domain.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends CrudRepository<User, Integer> {
    Optional<User> findBySocialId(String socialId);
}
