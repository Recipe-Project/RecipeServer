package com.recipe.app.src.user.application;

import com.recipe.app.src.user.application.dto.UserDto;
import com.recipe.app.src.user.domain.User;
import com.recipe.app.src.user.exception.ForbiddenAccessException;
import com.recipe.app.src.user.exception.NotFoundUserException;
import com.recipe.app.src.user.mapper.UserRepository;
import lombok.RequiredArgsConstructor;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;

    public User naverLogin(String accessToken, String fcmToken) throws IOException, ParseException {
        String header = "Bearer " + accessToken;
        String apiURL = "https://openapi.naver.com/v1/nid/me";

        Map<String, String> requestHeaders = new HashMap<>();
        requestHeaders.put("Authorization", header);

        URL url = new URL(apiURL);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();

        con.setRequestMethod("GET");
        for (Map.Entry<String, String> rqheader : requestHeaders.entrySet()) {
            con.setRequestProperty(rqheader.getKey(), rqheader.getValue());
        }

        int responseCode = con.getResponseCode();
        InputStreamReader streamReader;
        if (responseCode == HttpURLConnection.HTTP_OK) { // 정상 호출
            streamReader = new InputStreamReader(con.getInputStream());
        } else { // 에러 발생
            streamReader = new InputStreamReader(con.getErrorStream());
        }

        BufferedReader lineReader = new BufferedReader(streamReader);
        StringBuilder responseBody = new StringBuilder();

        String line;
        while ((line = lineReader.readLine()) != null) {
            responseBody.append(line);
        }

        String body = responseBody.toString();
        con.disconnect();

        JSONParser jsonParser = new JSONParser();
        JSONObject jsonObject = (JSONObject) jsonParser.parse(body);
        String resultcode = jsonObject.get("resultcode").toString();

        if (!resultcode.equals("00"))
            throw new ForbiddenAccessException();

        String response = jsonObject.get("response").toString();
        JSONObject responObj = (JSONObject) jsonParser.parse(response);
        String socialId = "naver_" + responObj.get("id").toString();
        String profilePhoto = null;
        String userName = responObj.get("name").toString();
        String email = responObj.get("email") != null ? responObj.get("email").toString() : null;
        String phoneNumber = responObj.get("mobile") != null ? responObj.get("mobile").toString() : null;

        User user = userRepository.findBySocialId(socialId).orElse(null);
        if (user == null) {
            user = userRepository.save(new User(socialId, profilePhoto, userName, email, phoneNumber, fcmToken));
        }

        return user;
    }

    public User kakaoLogin(String accessToken, String fcmToken) throws IOException, ParseException {

        String header = "Bearer " + accessToken;
        String apiURL = "https://kapi.kakao.com/v2/user/me";

        Map<String, String> requestHeaders = new HashMap<>();
        requestHeaders.put("Authorization", header);

        URL url = new URL(apiURL);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();

        con.setRequestMethod("GET");
        for (Map.Entry<String, String> rqheader : requestHeaders.entrySet()) {
            con.setRequestProperty(rqheader.getKey(), rqheader.getValue());
        }

        int responseCode = con.getResponseCode();
        InputStreamReader streamReader;
        if (responseCode == HttpURLConnection.HTTP_OK) { // 정상 호출
            streamReader = new InputStreamReader(con.getInputStream());
        } else { // 에러 발생
            streamReader = new InputStreamReader(con.getErrorStream());
        }

        BufferedReader lineReader = new BufferedReader(streamReader);
        StringBuilder responseBody = new StringBuilder();

        String line;
        while ((line = lineReader.readLine()) != null) {
            responseBody.append(line);
        }

        String body = responseBody.toString();

        con.disconnect();

        JSONParser jsonParser = new JSONParser();
        JSONObject jsonObject = (JSONObject) jsonParser.parse(body);
        String socialId = "kakao_" + jsonObject.get("id").toString();
        String response = jsonObject.get("kakao_account").toString();

        String profilePhoto = null;
        String email = null;
        String phoneNumber = null;

        JSONObject responObj = (JSONObject) jsonParser.parse(response);
        if (responObj.get("email") != null) {
            email = responObj.get("email").toString();
        }

        String profile = responObj.get("profile").toString();
        JSONObject profileObj = (JSONObject) jsonParser.parse(profile);
        String userName = profileObj.get("nickname").toString();

        User user = userRepository.findBySocialId(socialId).orElse(null);
        if (user == null) {
            user = userRepository.save(new User(socialId, profilePhoto, userName, email, phoneNumber, fcmToken));
        }

        return user;
    }

    public User googleLogin(String idToken, String fcmToken) throws IOException, ParseException {

        JSONParser jsonParser = new JSONParser();
        String url = "https://oauth2.googleapis.com/tokeninfo";
        url += "?id_token=" + idToken;

        URL gUrl = new URL(url);
        HttpURLConnection con = (HttpURLConnection) gUrl.openConnection();

        InputStream is = con.getInputStream();
        InputStreamReader isr = new InputStreamReader(is, "UTF-8");
        BufferedReader in = new BufferedReader(isr);

        JSONObject jsonObj = (JSONObject) jsonParser.parse(in);
        String userId = "google_" + jsonObj.get("sub").toString();
        String name = jsonObj.get("name").toString();
        String email = jsonObj.get("email").toString();
        String imageUrl = null;

        User user = userRepository.findBySocialId(userId).orElse(null);
        if (user == null) {
            user = userRepository.save(new User(userId, imageUrl, name, email, null, fcmToken));
        }

        return user;
    }

    public User updateUser(int userIdx, UserDto.UserProfileRequest request) {

        User user = retrieveUserByUserIdx(userIdx);

        user.changeProfile(request.getProfilePhoto(), request.getUserName());
        user = userRepository.save(user);

        return user;
    }

    /**
     * 회원 탈퇴 API
     * @param userIdx
     * @throws BaseException
     */
    @Transactional(rollbackFor = Exception.class)
    public void deleteUser(Integer jwtUserIdx, Integer userIdx) throws BaseException {
        //jwt 확인
        if(userIdx != (int)jwtUserIdx){
            throw new BaseException(FORBIDDEN_USER);
        }

        User user = userProvider.retrieveUserByUserIdx(jwtUserIdx);
        userRepository.delete(user);
    }

    public User retrieveUserByUserIdx(Integer userIdx) {
        return userRepository.findById(userIdx).orElseThrow(NotFoundUserException::new);
    }
}
