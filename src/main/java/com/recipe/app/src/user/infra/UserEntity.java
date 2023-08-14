package com.recipe.app.src.user.infra;

import com.recipe.app.common.entity.BaseEntity;
import com.recipe.app.src.fridge.infra.FridgeEntity;
import com.recipe.app.src.fridgeBasket.infra.FridgeBasketEntity;
import com.recipe.app.src.user.domain.User;
import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PUBLIC)
@EqualsAndHashCode(callSuper = false)
@Data
@Entity
@Table(name = "UserInfo")
public class UserEntity extends BaseEntity {
    @Id
    @Column(name = "userId", nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    @Column(name = "socialId", nullable = false, length = 32)
    private String socialId;

    @Column(name = "profileImgUrl")
    private String profileImgUrl;

    @Column(name = "nickname", nullable = false, length = 45)
    private String nickname;

    @Column(name = "email", length = 64)
    private String email;

    @Column(name = "phoneNumber", length = 16)
    private String phoneNumber;

    @Column(name = "deviceToken", length = 500)
    private String deviceToken;

    @Column(name = "recentLoginAt")
    private LocalDateTime recentLoginAt;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<FridgeEntity> fridges = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<FridgeBasketEntity> fridgeBaskets = new ArrayList<>();

    public User toModel() {
        return User.builder()
                .userId(userId)
                .socialId(socialId)
                .profileImgUrl(profileImgUrl)
                .nickname(nickname)
                .email(email)
                .phoneNumber(phoneNumber)
                .deviceToken(deviceToken)
                .createdAt(getCreatedAt())
                .updatedAt(getUpdatedAt())
                .recentLoginAt(recentLoginAt)
                .fridges(fridges.stream()
                        .map(FridgeEntity::toModel)
                        .collect(Collectors.toList()))
                .fridgeBaskets(fridgeBaskets.stream()
                        .map(FridgeBasketEntity::toModel)
                        .collect(Collectors.toList()))
                .build();
    }

    public static UserEntity fromModel(User user) {
        UserEntity userEntity = new UserEntity();
        userEntity.userId = user.getUserId();
        userEntity.socialId = user.getSocialId();
        userEntity.profileImgUrl = user.getProfileImgUrl();
        userEntity.nickname = user.getNickname();
        userEntity.email = user.getEmail();
        userEntity.phoneNumber = user.getPhoneNumber();
        userEntity.deviceToken = user.getDeviceToken();
        userEntity.setCreatedAt(user.getCreatedAt());
        userEntity.setUpdatedAt(user.getUpdatedAt());
        userEntity.recentLoginAt = user.getRecentLoginAt();
        userEntity.fridges = user.getFridges().stream()
                .map(FridgeEntity::fromModel)
                .collect(Collectors.toList());
        userEntity.fridgeBaskets = user.getFridgeBaskets().stream()
                .map(FridgeBasketEntity::fromModel)
                .collect(Collectors.toList());
        return userEntity;
    }
}
