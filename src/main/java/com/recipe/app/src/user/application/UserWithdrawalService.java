package com.recipe.app.src.user.application;

import com.recipe.app.src.user.domain.UserWithdrawal;
import com.recipe.app.src.user.infra.UserWithdrawalRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserWithdrawalService {

    private final UserWithdrawalRepository userWithdrawalRepository;

    public UserWithdrawalService(UserWithdrawalRepository userWithdrawalRepository) {
        this.userWithdrawalRepository = userWithdrawalRepository;
    }

    @Transactional
    public void saveWithdrawalReason(Long userId, String withdrawalReason) {

        UserWithdrawal userWithdrawal = UserWithdrawal.builder()
                .userId(userId)
                .withdrawalReason(withdrawalReason)
                .build();

        userWithdrawalRepository.save(userWithdrawal);
    }
}
