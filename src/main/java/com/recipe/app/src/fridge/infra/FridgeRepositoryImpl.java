package com.recipe.app.src.fridge.infra;

import com.recipe.app.src.fridge.application.port.FridgeRepository;
import com.recipe.app.src.fridge.domain.Fridge;
import com.recipe.app.src.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Repository
@RequiredArgsConstructor
public class FridgeRepositoryImpl implements FridgeRepository {

    private final FridgeJpaRepository fridgeJpaRepository;

    @Override
    public List<Fridge> findByUser(User user) {
        return fridgeJpaRepository.findByUser(User.fromModel(user)).stream().map(FridgeEntity::toModel).collect(Collectors.toList());
    }

    @Override
    public List<Fridge> saveAll(List<Fridge> fridges) {
        return StreamSupport.stream(fridgeJpaRepository.saveAll(fridges.stream().map(FridgeEntity::fromModel).collect(Collectors.toList())).spliterator(), false)
                .map(FridgeEntity::toModel)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Fridge> findByUserAndFridgeId(User user, Long fridgeId) {
        return fridgeJpaRepository.findByUserAndFridgeId(User.fromModel(user), fridgeId).map(FridgeEntity::toModel);
    }

    @Override
    public void delete(Fridge fridge) {
        fridgeJpaRepository.delete(FridgeEntity.fromModel(fridge));
    }

    @Override
    public void save(Fridge fridge) {
        fridgeJpaRepository.save(FridgeEntity.fromModel(fridge));
    }
}
