package com.recipe.app.src.user.domain;

import com.google.common.base.Preconditions;
import com.recipe.app.common.entity.BaseEntity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.util.StringUtils;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDateTime;
import java.util.Objects;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "UserInfo")
public class User extends BaseEntity {
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

    @Builder
    public User(Long userId, String socialId, String profileImgUrl, String nickname, String email, String phoneNumber, String deviceToken, LocalDateTime recentLoginAt) {

        Preconditions.checkArgument(StringUtils.hasText(socialId), "소셜 로그인 ID 값을 입력해주세요.");
        Preconditions.checkArgument(StringUtils.hasText(nickname), "닉네임을 입력해주세요.");
        
        this.userId = userId;
        this.socialId = socialId;
        this.profileImgUrl = profileImgUrl;
        this.nickname = nickname;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.deviceToken = deviceToken;
        this.recentLoginAt = recentLoginAt;
    }

    public void changeProfile(String profileImgUrl, String nickname) {

        Preconditions.checkArgument(StringUtils.hasText(nickname), "닉네임을 입력해주세요.");
        
        this.profileImgUrl = profileImgUrl;
        this.nickname = nickname;
    }

    public void changeRecentLoginAt(LocalDateTime recentLoginAt) {

        Objects.requireNonNull(recentLoginAt, "최근 로그인 시간을 입력해주세요.");
        
        this.recentLoginAt = recentLoginAt;
    }

    public void changeDeviceToken(String deviceToken) {

        Preconditions.checkArgument(StringUtils.hasText(deviceToken), "FCM 토큰을 입력해주세요.");

        this.deviceToken = deviceToken;
    }
}
