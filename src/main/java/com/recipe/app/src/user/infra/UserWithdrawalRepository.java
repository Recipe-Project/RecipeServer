package com.recipe.app.src.user.infra;

import com.recipe.app.src.user.domain.UserWithdrawal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserWithdrawalRepository extends JpaRepository<UserWithdrawal, Integer> {
}
