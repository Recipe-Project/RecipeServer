package com.recipe.app.src.user;

import com.recipe.app.config.BaseException;
import com.recipe.app.config.secret.Secret;
import com.recipe.app.src.user.models.*;
import com.recipe.app.utils.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.recipe.app.config.BaseResponseStatus.*;

import java.io.*;
import java.net.*;
import java.util.HashMap;
import java.util.Map;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final UserProvider userProvider;
    private final JwtService jwtService;

    @Autowired
    public UserService(UserRepository userRepository, UserProvider userProvider, JwtService jwtService) {
        this.userRepository = userRepository;
        this.userProvider = userProvider;
        this.jwtService = jwtService;
    }

    /**
     * 네이버 로그인
     * @param accessToken
     * @return PostUserRes
     * @throws BaseException
     */
    public PostUserRes naverLogin(String accessToken) throws BaseException {
        JSONObject jsonObject;
        String resultcode;

        String header = "Bearer " + accessToken; // Bearer 다음에 공백 추가
        String apiURL = "https://openapi.naver.com/v1/nid/me";

        Map<String, String> requestHeaders = new HashMap<>();
        requestHeaders.put("Authorization", header);

        HttpURLConnection con;
        try {
            URL url = new URL(apiURL);
            con = (HttpURLConnection) url.openConnection();
        } catch (MalformedURLException e) {
            throw new BaseException(WRONG_URL);
        } catch (IOException e) {
            throw new BaseException(FAILED_TO_CONNECT);
        }

        String body;
        try {
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

            body = responseBody.toString();
        } catch (IOException e) {
            throw new BaseException(FAILED_TO_READ_RESPONSE);
        } finally {
            con.disconnect();
        }

        if (body.length() == 0) {
            throw new BaseException(FAILED_TO_READ_RESPONSE);
        }

        try{
            JSONParser jsonParser = new JSONParser();
            jsonObject = (JSONObject) jsonParser.parse(body);
            resultcode = jsonObject.get("resultcode").toString();
            System.out.println(resultcode);
        }
        catch (Exception e){
            throw new BaseException(FAILED_TO_PARSE);
        }

        String response;
        if(resultcode.equals("00")){
            response = jsonObject.get("response").toString();
            System.out.println(response);
        }
        else{
            throw new BaseException(FORBIDDEN_ACCESS);
        }

        String socialId;
        String profilePhoto;
        String userName;
        String email;
        String phoneNumber;
        try {
            JSONParser jsonParser = new JSONParser();
            JSONObject responObj = (JSONObject) jsonParser.parse(response);
            socialId = "naver_"+responObj.get("id").toString();
            profilePhoto = responObj.get("profile_image").toString();
            userName = responObj.get("name").toString();
            email = responObj.get("email").toString();
            phoneNumber = responObj.get("mobile").toString();
        }
        catch (Exception e){
            throw new BaseException(FAILED_TO_PARSE);
        }

        User user = null;
        try {
            //이미 존재하는 회원이 있는지 조회
            user = userRepository.findBySocialId(socialId);
        } catch (Exception ignored) {
            throw new BaseException(DATABASE_ERROR);
        }

        // 이미 존재하는 회원이 없다면 유저 정보 저장
        if (user == null) {
            user = new User(socialId, profilePhoto, userName, email, phoneNumber);

            try {
                user = userRepository.save(user);
            } catch (Exception exception) {
                throw new BaseException(DATABASE_ERROR);
            }
        }
        else{
            if(!user.getStatus().equals("ACTIVE")) {
                user.setStatus("ACTIVE");

                try {
                    user = userRepository.save(user);
                } catch (Exception exception) {
                    throw new BaseException(DATABASE_ERROR);
                }
            }
        }

        // JWT 생성
        try {
            Integer userIdx = user.getUserIdx();
            String jwt = jwtService.createJwt(userIdx);
            return new PostUserRes(userIdx, jwt);
        }catch(Exception e){
            throw new BaseException(FAILED_TO_NAVER_LOGIN);
        }
    }

    /**
     * 카카오 로그인
     * @param accessToken
     * @return PostUserRes
     * @throws BaseException
     */
    public PostUserRes kakaoLogin(String accessToken) throws BaseException {
        JSONObject jsonObject;

        String header = "Bearer " + accessToken; // Bearer 다음에 공백 추가
        String apiURL = "https://kapi.kakao.com/v2/user/me";

        Map<String, String> requestHeaders = new HashMap<>();
        requestHeaders.put("Authorization", header);

        HttpURLConnection con;
        try {
            URL url = new URL(apiURL);
            con = (HttpURLConnection) url.openConnection();
        } catch (MalformedURLException e) {
            throw new BaseException(WRONG_URL);
        } catch (IOException e) {
            throw new BaseException(FAILED_TO_CONNECT);
        }

        String body;
        try {
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

            body = responseBody.toString();
        } catch (IOException e) {
            throw new BaseException(FAILED_TO_READ_RESPONSE);
        } finally {
            con.disconnect();
        }

        if (body.length() == 0) {
            throw new BaseException(FAILED_TO_READ_RESPONSE);
        }

        String socialId;
        String response;
        try{
            JSONParser jsonParser = new JSONParser();
            jsonObject = (JSONObject) jsonParser.parse(body);
            socialId = "kakao_"+jsonObject.get("id").toString();
            response = jsonObject.get("kakao_account").toString();
        }
        catch (Exception e){
            throw new BaseException(FAILED_TO_PARSE);
        }

        String profilePhoto;
        String userName;
        String email;
        String phoneNumber;
        try {
            JSONParser jsonParser = new JSONParser();
            JSONObject responObj = (JSONObject) jsonParser.parse(response);
            email = responObj.get("email").toString();
            phoneNumber = responObj.get("phone_number").toString();

            String profile = responObj.get("profile").toString();
            JSONObject profileObj = (JSONObject) jsonParser.parse(profile);
            profilePhoto = profileObj.get("profile_image").toString();
            userName = profileObj.get("nickname").toString();

        }
        catch (Exception e){
            throw new BaseException(FAILED_TO_PARSE);
        }

        User user = null;
        try {
            //이미 존재하는 회원이 있는지 조회
            user = userRepository.findBySocialId(socialId);
        } catch (Exception ignored) {
            throw new BaseException(DATABASE_ERROR);
        }

        // 이미 존재하는 회원이 없다면 유저 정보 저장
        if (user == null) {
            user = new User(socialId, profilePhoto, userName, email, phoneNumber);

            try {
                user = userRepository.save(user);
            } catch (Exception exception) {
                throw new BaseException(DATABASE_ERROR);
            }
        }
        else{
            if(!user.getStatus().equals("ACTIVE")) {
                user.setStatus("ACTIVE");

                try {
                    user = userRepository.save(user);
                } catch (Exception exception) {
                    throw new BaseException(DATABASE_ERROR);
                }
            }
        }

        // JWT 생성
        try {
            Integer userIdx = user.getUserIdx();
            String jwt = jwtService.createJwt(userIdx);
            return new PostUserRes(userIdx, jwt);
        }catch(Exception e){
            throw new BaseException(FAILED_TO_KAKAO_LOGIN);
        }
    }

}
