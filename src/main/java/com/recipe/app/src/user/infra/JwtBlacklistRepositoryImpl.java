package com.recipe.app.src.user.infra;

import com.recipe.app.src.user.application.port.JwtBlacklistRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class JwtBlacklistRepositoryImpl implements JwtBlacklistRepository {

    private final JwtBlacklistJpaRepository jwtBlacklistJpaRepository;

    @Override
    public Optional<String> findById(String id) {
        return jwtBlacklistJpaRepository.findById(id).map(JwtEntity::getJwt);
    }

    @Override
    public void save(String jwt) {
        jwtBlacklistJpaRepository.save(JwtEntity.fromModel(jwt));
    }
}
