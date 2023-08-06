package com.recipe.app.src.user.infra;

import com.recipe.app.common.entity.BaseEntity;
import com.recipe.app.src.fridge.domain.Fridge;
import com.recipe.app.src.fridgeBasket.domain.FridgeBasket;
import com.recipe.app.src.scrap.domain.ScrapBlog;
import com.recipe.app.src.scrap.domain.ScrapPublic;
import com.recipe.app.src.scrap.domain.ScrapYoutube;
import com.recipe.app.src.user.domain.User;
import com.recipe.app.src.userRecipe.domain.UserRecipe;
import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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

    public UserEntity(String socialId, String profileImgUrl, String nickname, String email, String phoneNumber, String deviceToken) {
        this.socialId = socialId;
        this.profileImgUrl = profileImgUrl;
        this.nickname = nickname;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.deviceToken = deviceToken;
    }

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
        return userEntity;
    }
}