package com.recipe.app.src.user.domain;

import java.util.Random;

public enum ProfileImage {

    TOMATO("https://firebasestorage.googleapis.com/v0/b/recipeapp-a79ed.appspot.com/o/profile_icon%2Favatar.png?alt=media&token=d29802a8-9c30-41f1-ab04-5ccf62c3dad4"),
    ORANGE("https://firebasestorage.googleapis.com/v0/b/recipeapp-a79ed.appspot.com/o/profile_icon%2Favatar%20(1).png?alt=media&token=4eec78f8-597b-40af-8a8f-436ee80d60d6"),
    SHRIMP("https://firebasestorage.googleapis.com/v0/b/recipeapp-a79ed.appspot.com/o/profile_icon%2Favatar%20(2).png?alt=media&token=b91f794c-1f83-4910-b146-29a1343906a3"),
    CHEESE("https://firebasestorage.googleapis.com/v0/b/recipeapp-a79ed.appspot.com/o/profile_icon%2Favatar%20(3).png?alt=media&token=9ccba668-6eef-4fa7-8c8d-396aa251d12f"),
    SWEET_POTATO("https://firebasestorage.googleapis.com/v0/b/recipeapp-a79ed.appspot.com/o/profile_icon%2Favatar%20(4).png?alt=media&token=18073140-e65d-46c0-a397-a65951ec075b"),
    POTATO("https://firebasestorage.googleapis.com/v0/b/recipeapp-a79ed.appspot.com/o/profile_icon%2Favatar%20(5).png?alt=media&token=71a5f924-3e8b-4549-be70-9b03246017b1"),
    PORK("https://firebasestorage.googleapis.com/v0/b/recipeapp-a79ed.appspot.com/o/profile_icon%2Favatar%20(6).png?alt=media&token=6d51c5ce-d610-4a04-9729-976851bfeead"),
    CLAM("https://firebasestorage.googleapis.com/v0/b/recipeapp-a79ed.appspot.com/o/profile_icon%2Favatar%20(7).png?alt=media&token=27890682-4c8f-4749-98fd-d99b232ab0e3");

    private final String profileImgUrl;

    ProfileImage(String profileImgUrl) {
        this.profileImgUrl = profileImgUrl;
    }

    public static String getInitProfileImgUrl() {

        return values()[new Random().nextInt(ProfileImage.values().length)].profileImgUrl;
    }
}
