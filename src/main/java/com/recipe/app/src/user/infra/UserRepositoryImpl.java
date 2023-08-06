package com.recipe.app.src.user.infra;

import com.recipe.app.src.user.application.port.UserRepository;
import com.recipe.app.src.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository {

    private final UserJpaRepository userJpaRepository;

    @Override
    public Optional<User> findBySocialId(String socialId) {
        return userJpaRepository.findBySocialId(socialId).map(UserEntity::toModel);
    }

    @Override
    public Optional<User> findById(Long id) {
        return userJpaRepository.findById(id).map(UserEntity::toModel);
    }

    @Override
    public User save(User user) {
        return userJpaRepository.save(UserEntity.fromModel(user)).toModel();
    }

    @Override
    public void delete(User user) {
        userJpaRepository.delete(UserEntity.fromModel(user));
    }
}
