package com.recipe.app.src.user.models;

import lombok.*;
import javax.persistence.*;
import com.recipe.app.config.BaseEntity;

@NoArgsConstructor(access = AccessLevel.PUBLIC) // Unit Test 를 위해 PUBLIC
@EqualsAndHashCode(callSuper = false)
@Data // from lombok
@Entity // 필수, Class 를 Database Table화 해주는 것이다
@Table(name = "UserInfo") // Table 이름을 명시해주지 않으면 class 이름을 Table 이름으로 대체한다.
public class User extends BaseEntity {
    @Id // PK를 의미하는 어노테이션
    @Column(name = "userIdx", nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer userIdx;

    @Column(name="socialId", nullable = false, length=45)
    private String socialId;

    @Column(name = "profilePhoto", nullable = false)
    private String profilePhoto;

    @Column(name = "userName", nullable = false, length = 45)
    private String userName;

    @Column(name = "email", length = 45)
    private String email;

    @Column(name = "phoneNumber", nullable = false, length = 45)
    private String phoneNumber;

    @Column(name="status", nullable=false, length=10)
    private String status="ACTIVE";

    public User(String socialId, String profilePhoto, String userName, String email, String phoneNumber){
        this.socialId = socialId;
        this.profilePhoto = profilePhoto;
        this.userName = userName;
        this.email = email;
        this.phoneNumber = phoneNumber;
    }

}
