package com.recipe.app.src.user.application;

import com.recipe.app.src.user.application.dto.UserDto;
import com.recipe.app.src.user.application.port.JwtBlacklistRepository;
import com.recipe.app.src.user.application.port.UserRepository;
import com.recipe.app.src.user.domain.User;
import com.recipe.app.src.user.exception.ForbiddenAccessException;
import lombok.RequiredArgsConstructor;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final JwtBlacklistRepository jwtBlacklistRepository;
    @Value("${kakao.client-id}")
    private String kakaoClientId;
    @Value("${kakao.redirect-uri}")
    private String kakaoRedirectURI;
    @Value("${naver.client-id}")
    private String naverClientId;
    @Value("${naver.client-secret}")
    private String naverClientSecret;
    @Value("${kakao.redirect-uri}")
    private String naverRedirectURI;

    @Transactional
    public User autoLogin(User user) {
        user = user.changeRecentLoginAt();
        return userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public String getNaverAccessToken(String code, String state) throws IOException, ParseException {

        String apiURL = "https://nid.naver.com/oauth2.0/token" +
                "?grant_type=authorization_code" +
                "&client_id=" + naverClientId +
                "&client_secret=" + naverClientSecret +
                "&redirect_uri=" + naverRedirectURI +
                "&code=" + code +
                "&state=" + state;

        URL url = new URL(apiURL);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();

        con.setRequestMethod("GET");

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
        System.out.println(body);
        con.disconnect();

        JSONParser jsonParser = new JSONParser();
        JSONObject jsonObject = (JSONObject) jsonParser.parse(body);
        String accessToken = jsonObject.get("access_token").toString();
        return accessToken;
    }

    @Transactional
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
        String profileImgUrl = null;
        String nickname = responObj.get("name").toString();
        String email = responObj.get("email") != null ? responObj.get("email").toString() : null;
        String phoneNumber = responObj.get("mobile") != null ? responObj.get("mobile").toString() : null;

        return userRepository.findBySocialId(socialId).orElseGet(() ->
                userRepository.save(User.from(socialId, profileImgUrl, nickname, email, phoneNumber, fcmToken)));
    }

    @Transactional(readOnly = true)
    public String getKakaoAccessToken(String code) throws IOException, ParseException {

        String apiURL = "https://kauth.kakao.com/oauth/token";

        URL url = new URL(apiURL);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();

        con.setRequestMethod("POST");
        con.setDoOutput(true);
        con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");

        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(con.getOutputStream()));
        StringBuilder sb = new StringBuilder();
        sb.append("grant_type=authorization_code");
        sb.append("&client_id=" + kakaoClientId);
        sb.append("&redirect_uri=" + kakaoRedirectURI);
        sb.append("&code=" + code);

        bw.write(sb.toString());
        bw.flush();
        bw.close();

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
        String accessToken = jsonObject.get("access_token").toString();
        return accessToken;
    }

    @Transactional
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
        System.out.println(jsonObject.toJSONString());
        String socialId = "kakao_" + jsonObject.get("id").toString();
        String response = jsonObject.get("kakao_account").toString();

        String profileImgUrl = null;
        String phoneNumber = null;

        JSONObject responObj = (JSONObject) jsonParser.parse(response);
        String email = responObj.get("email") != null ? responObj.get("email").toString() : null;
        String profile = responObj.get("profile").toString();
        JSONObject profileObj = (JSONObject) jsonParser.parse(profile);
        String nickname = profileObj.get("nickname").toString();

        return userRepository.findBySocialId(socialId).orElseGet(() ->
                userRepository.save(User.from(socialId, profileImgUrl, nickname, email, phoneNumber, fcmToken)));
    }

    @Transactional
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
        String socialId = "google_" + jsonObj.get("sub").toString();
        String nickname = jsonObj.get("name").toString();
        String email = jsonObj.get("email").toString();
        String profileImgUrl = null;
        String phoneNumber = null;

        return userRepository.findBySocialId(socialId).orElseGet(() ->
                userRepository.save(User.from(socialId, profileImgUrl, nickname, email, phoneNumber, fcmToken)));
    }

    @Transactional
    public User updateUser(User user, UserDto.UserProfileRequest request) {
        user = user.changeProfile(request.getProfileImgUrl(), request.getNickname());
        return userRepository.save(user);
    }

    @Transactional
    public void deleteUser(User user, String jwt) {
        userRepository.delete(user);
        registerJwtBlackList(jwt);
    }

    @Transactional
    public void registerJwtBlackList(String jwt) {
        jwtBlacklistRepository.save(jwt);
    }

    @Transactional
    public void updateFcmToken(UserDto.UserDeviceTokenRequest request, User user) {
        user = user.changeDeviceToken(request.getFcmToken());
        userRepository.save(user);
    }
}
