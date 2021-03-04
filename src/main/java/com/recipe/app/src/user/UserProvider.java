package com.recipe.app.src.user;

import com.recipe.app.config.BaseException;
import com.recipe.app.config.secret.Secret;
import com.recipe.app.src.user.models.*;
import com.recipe.app.utils.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.recipe.app.config.BaseResponseStatus.*;

@Service
public class UserProvider {
    private final UserRepository userRepository;
    private final JwtService jwtService;

    @Autowired
    public UserProvider(UserRepository userRepository, JwtService jwtService) {
        this.userRepository = userRepository;
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
        if (user == null || !user.getStatus().equals("ACTIVE")) {
            throw new BaseException(NOT_FOUND_USER);
        }

        // 3. User를 return
        return user;
    }

    /**
     * 자동 로그인
     * @return void
     * @throws BaseException
     */
    public void autoLogin() throws BaseException {
        Integer userIdx = jwtService.getUserId();
        retrieveUserByUserIdx(userIdx);
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
            existsUserInfoList = userRepository.findBySocialIdAndStatus(userID, "ACTIVE");
        } catch (Exception ignored) {
            throw new BaseException(DATABASE_ERROR);
        }

        // 2. 존재하는 UserInfo가 있는지 확인
        User userInfo;
        if (existsUserInfoList != null && existsUserInfoList.size() > 0) {
            userInfo = existsUserInfoList.get(0);
        } else {
            userInfo = null;

        }

        // 3. UserInfo를 return
        return userInfo;
    }

    /**
     * Idx로 회원 조회
     * @param userIdx
     * @return GetUserRes
     * @throws BaseException
     */
    public GetUserRes retrieveUser(Integer userIdx) throws BaseException {
        User user = retrieveUserByUserIdx(jwtService.getUserId());
        if(userIdx != user.getUserIdx()){
            throw new BaseException(FORBIDDEN_USER);
        }

        try {
            String socialIdx = user.getSocialId();
            String profilePhoto = user.getProfilePhoto();
            String userName = user.getUserName();
            String email = user.getEmail();
            String phoneNumber = user.getPhoneNumber();

            return new GetUserRes(userIdx, socialIdx, profilePhoto, userName, email, phoneNumber);
        }catch(Exception e){
            throw new BaseException(FAILED_TO_GET_USER);
        }
    }
}
