package com.recipe.app.src.user.models;

import com.recipe.app.common.entity.BaseEntity;
import com.recipe.app.src.fridge.models.Fridge;
import com.recipe.app.src.fridgeBasket.models.FridgeBasket;
import com.recipe.app.src.receipt.models.Receipt;
import com.recipe.app.src.scrapBlog.models.ScrapBlog;
import com.recipe.app.src.scrapPublic.models.ScrapPublic;
import com.recipe.app.src.scrapYoutube.models.ScrapYoutube;
import com.recipe.app.src.userRecipe.models.UserRecipe;
import com.recipe.app.src.viewBlog.models.ViewBlog;
import com.recipe.app.src.viewPublic.models.ViewPublic;
import com.recipe.app.src.viewYoutube.models.ViewYoutube;
import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

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

    //추가
    @Column(name = "deviceToken", length = 500)
    private String deviceToken;

    @Column(name="status", nullable=false, length=10)
    private String status="ACTIVE";

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<UserRecipe> userRecipes = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<ScrapYoutube> scrapYoutubes = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<ScrapBlog> scrapBlogs = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<ScrapPublic> scrapPublics = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<FridgeBasket> fridgeBasket = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<ViewBlog> viewBlogs = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Receipt> receipts = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<ViewYoutube> viewYoutubes = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<ViewPublic> viewPublics = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Fridge> fridges = new ArrayList<>();

    public User(String socialId, String profilePhoto, String userName, String email, String phoneNumber, String deviceToken){
        this.socialId = socialId;
        this.profilePhoto = profilePhoto;
        this.userName = userName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.deviceToken = deviceToken;//추가
    }

}
