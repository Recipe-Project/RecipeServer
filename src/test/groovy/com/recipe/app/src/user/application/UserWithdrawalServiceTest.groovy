package com.recipe.app.src.user.application

import com.recipe.app.src.user.domain.UserWithdrawal
import com.recipe.app.src.user.infra.UserWithdrawalRepository
import spock.lang.Specification

class UserWithdrawalServiceTest extends Specification {

    private UserWithdrawalRepository userWithdrawalRepository = Mock()
    private UserWithdrawalService userWithdrawalService = new UserWithdrawalService(userWithdrawalRepository)

    def "탈퇴 사유 저장"() {

        given:
        Long userId = 1L
        String withdrawalReason = "서비스가 만족스럽지 않아서"

        when:
        userWithdrawalService.saveWithdrawalReason(userId, withdrawalReason)

        then:
        1 * userWithdrawalRepository.save(_) >> { args ->
            def userWithdrawal = args.get(0) as UserWithdrawal

            userWithdrawal.userId == userId
            userWithdrawal.withdrawalReason == withdrawalReason
            userWithdrawal.createdAt != null
        }
    }

    def "탈퇴 사유 저장 - 사유가 null인 경우도 저장"() {

        given:
        Long userId = 1L
        String withdrawalReason = null

        when:
        userWithdrawalService.saveWithdrawalReason(userId, withdrawalReason)

        then:
        1 * userWithdrawalRepository.save(_) >> { args ->
            def userWithdrawal = args.get(0) as UserWithdrawal

            userWithdrawal.userId == userId
            userWithdrawal.withdrawalReason == null
            userWithdrawal.createdAt != null
        }
    }
}
