package com.recipe.app.src.user.domain;

import com.recipe.app.common.entity.BaseEntity;
import com.recipe.app.src.fridge.domain.Fridge;
import com.recipe.app.src.fridgeBasket.domain.FridgeBasket;
import com.recipe.app.src.scrap.domain.ScrapBlog;
import com.recipe.app.src.scrap.domain.ScrapPublic;
import com.recipe.app.src.scrap.domain.ScrapYoutube;
import com.recipe.app.src.userRecipe.domain.UserRecipe;
import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PUBLIC)
@EqualsAndHashCode(callSuper = false)
@Data
@Entity
@Table(name = "UserInfo")
public class User extends BaseEntity {
    @Id
    @Column(name = "userIdx", nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int userIdx;

    @Column(name = "socialId", nullable = false, length = 45)
    private String socialId;

    @Column(name = "profilePhoto", nullable = false)
    private String profilePhoto;

    @Column(name = "userName", nullable = false, length = 45)
    private String userName;

    @Column(name = "email", length = 45)
    private String email;

    @Column(name = "phoneNumber", nullable = false, length = 45)
    private String phoneNumber;

    @Column(name = "deviceToken", length = 500)
    private String deviceToken;

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
    private List<Fridge> fridges = new ArrayList<>();

    public User(String socialId, String profilePhoto, String userName, String email, String phoneNumber, String deviceToken) {
        this.socialId = socialId;
        this.profilePhoto = profilePhoto;
        this.userName = userName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.deviceToken = deviceToken;
    }

    public void changeProfile(String profilePhoto, String userName) {
        this.profilePhoto = profilePhoto;
        this.userName = userName;
    }

    public boolean hasFridgeBaskets() {
        return !fridgeBasket.isEmpty();
    }
}
