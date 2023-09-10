package com.recipe.app.src.user;

import com.recipe.app.config.BaseException;
import com.recipe.app.config.secret.Secret;
import com.recipe.app.src.user.models.*;
import com.recipe.app.src.userRecipe.UserRecipeRepository;
import com.recipe.app.src.userRecipe.models.UserRecipe;
import com.recipe.app.utils.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.*;

import static com.recipe.app.config.BaseResponseStatus.*;

@Service
public class UserProvider {
    private final UserRepository userRepository;
    private final UserRecipeRepository userRecipeRepository;
    private final JwtService jwtService;

    @Autowired
    public UserProvider(UserRepository userRepository, UserRecipeRepository userRecipeRepository, JwtService jwtService) {
        this.userRepository = userRepository;
        this.userRecipeRepository = userRecipeRepository;
        this.jwtService = jwtService;
    }

    /**
     * Idx로 회원 조회
     *
     * @param userIdx
     * @return User
     * @throws BaseException
     */
    public User retrieveUserByUserIdx(Integer userIdx) throws BaseException {
        // 1. DB에서 User 조회
        User user;
        try {
            user = userRepository.findById(userIdx).orElse(null);
        } catch (Exception ignored) {
            throw new BaseException(DATABASE_ERROR);
        }

        // 2. 존재하는 회원인지 확인
        if (user == null) {
            throw new BaseException(NOT_FOUND_USER);
        }
        else {
            if (user.getStatus().equals("SUSPEND"))
                throw new BaseException(SUSPENDED_USER);
        }

        // 3. User를 return
        return user;
    }

    /**
     * 유저조회
     * @return User
     * @throws BaseException
     */
    public User retrieveUserInfoBySocialId(String userID) throws BaseException {
        // 1. userId 이용해서 UserInfo DB 접근
        List<User> existsUserInfoList;
        try {
            existsUserInfoList = userRepository.findBySocialId(userID);
        } catch (Exception ignored) {
            throw new BaseException(DATABASE_ERROR);
        }

        // 2. 존재하는 UserInfo가 있는지 확인
        User userInfo;
        if (existsUserInfoList != null && existsUserInfoList.size() > 0) {
            userInfo = existsUserInfoList.get(0);
            if (userInfo.getStatus().equals("SUSPEND"))
                throw new BaseException(SUSPENDED_USER);
        } else {
            userInfo = null;
        }

        // 3. UserInfo를 return
        return userInfo;
    }

    /**
     * Idx로 회원 마이페이지 조회
     * @param userIdx
     * @return GetUserRes
     * @throws BaseException
     */
    public GetUserRes retrieveUser(int jwtUserIdx, Integer userIdx) throws BaseException {
        if(userIdx != jwtUserIdx){
            throw new BaseException(FORBIDDEN_USER);
        }
        User user = retrieveUserByUserIdx(jwtUserIdx);

        try {
            String profilePhoto = user.getProfilePhoto();
            String userName = user.getUserName();
            Integer youtubeScrapCnt=0;
            for(int i=0;i<user.getScrapYoutubes().size();i++){
                if(user.getScrapYoutubes().get(i).getStatus().equals("ACTIVE")){
                    youtubeScrapCnt++;
                }
            }
            Integer blogScrapCnt = 0;
            for(int i=0;i<user.getScrapBlogs().size();i++){
                if(user.getScrapBlogs().get(i).getStatus().equals("ACTIVE")){
                    blogScrapCnt++;
                }
            }
            Integer recipeScrapCnt = 0;
            for(int i=0;i<user.getScrapPublics().size();i++){
                if(user.getScrapPublics().get(i).getStatus().equals("ACTIVE")){
                    recipeScrapCnt++;
                }
            }

            List<UserRecipe> userRecipes = user.getUserRecipes();
            Collections.sort(userRecipes, new Comparator<UserRecipe>() {
                @Override
                public int compare(UserRecipe o1, UserRecipe o2) {
                    return -(o1.getCreatedAt().toString().compareTo(o2.getCreatedAt().toString()));
                }
            });
            int totalSize = 0;
            for(int i=0;i<userRecipes.size();i++){
                if(userRecipes.get(i).getStatus().equals("ACTIVE")){
                    totalSize++;
                }
            }
            int size=0;

            List<MypageMyRecipeList> myRecipeList= new ArrayList<>();
            for(int i=0;i<userRecipes.size();i++){
                if(size==6){
                    break;
                }
                if(userRecipes.get(i).getStatus().equals("ACTIVE")) {
                    Integer myRecipeIdx = userRecipes.get(i).getUserRecipeIdx();
                    String myRecipeThumbnail = userRecipes.get(i).getThumbnail();
                    MypageMyRecipeList mypageMyRecipe = new MypageMyRecipeList(myRecipeIdx, myRecipeThumbnail);

                    myRecipeList.add(mypageMyRecipe);
                    size++;
                }
            }

            return new GetUserRes(userIdx, profilePhoto, userName, youtubeScrapCnt, blogScrapCnt, recipeScrapCnt, totalSize, myRecipeList);
        }catch(Exception e){
            throw new BaseException(FAILED_TO_GET_USER);
        }
    }
}
