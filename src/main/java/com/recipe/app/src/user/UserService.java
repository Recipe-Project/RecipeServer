package com.recipe.app.src.user;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
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
        String profilePhoto=null;
        String userName;
        String email=null;
        String phoneNumber=null;
        try {
            JSONParser jsonParser = new JSONParser();
            JSONObject responObj = (JSONObject) jsonParser.parse(response);
            socialId = "naver_"+responObj.get("id").toString();
            if(responObj.get("profile_image")!=null) {
                profilePhoto = responObj.get("profile_image").toString();
            }
            userName = responObj.get("name").toString();
            if(responObj.get("email")!=null) {
                email = responObj.get("email").toString();
            }
            if(responObj.get("mobile")!=null) {
                phoneNumber = responObj.get("mobile").toString();
            }
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
        System.out.println(body);

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

        String profilePhoto=null;
        String userName=null;
        String email=null;
        String phoneNumber=null;
        try {
            JSONParser jsonParser = new JSONParser();
            JSONObject responObj = (JSONObject) jsonParser.parse(response);
            if(responObj.get("email")!=null) {
                email = responObj.get("email").toString();
            }

            String profile = responObj.get("profile").toString();
            JSONObject profileObj = (JSONObject) jsonParser.parse(profile);
            userName = profileObj.get("nickname").toString();

            if(profileObj.get("profile_image")!=null) {
                profilePhoto = profileObj.get("profile_image").toString();
            }

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


    /**
     * 구글 로그인
     * @param accessToken
     * @return PostUserRes
     * @throws BaseException
     */
    public PostUserRes googleLogin (String accessToken)  throws BaseException {
        //요청하는 클라이언트마다 가진 정보가 다를 수 있기에 HashMap타입으로 선언
        HashMap<String, Object> googleUserInfo = new HashMap<>();
        // String reqURL = "https://www.googleapis.com/oauth2/v1/userinfo?alt=json&access_token="+access_Token;
        String reqURL = "https://www.googleapis.com/userinfo/v2/me?access_token="+accessToken;

        try {
            URL url = new URL(reqURL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            //요청에 필요한 Header에 포함될 내용
            conn.setRequestProperty("Authorization", "Bearer " + accessToken);
            int responseCode = conn.getResponseCode();
            System.out.println("responseCode : "+responseCode);
            if(responseCode == 200) {
                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));

                String line = "";
                String result = "";

                while ((line = br.readLine()) != null) {
                    result += line;
                }
                JsonParser parser = new JsonParser();
                System.out.println("result : " + result);
                JsonElement element = parser.parse(result);

                String name = element.getAsJsonObject().get("name").getAsString();
                String email = element.getAsJsonObject().get("email").getAsString();
                String googleId = "GOOGLE_" + element.getAsJsonObject().get("id").getAsString();

                googleUserInfo.put("name", name);
                googleUserInfo.put("email", email);
                googleUserInfo.put("googleId", googleId);

                System.out.println("login Controller : " + googleUserInfo);

                User existsUserInfo = null;

                existsUserInfo = userProvider.retrieveUserInfoBySocialId(googleId);
                // 1-1. 존재하는 회원이 없다면 회원가입
                if (existsUserInfo == null) {
                    // 빈 값은 null 처리
                    User userInfo = new User(googleId, null, name,email,null);

                    // 2. 유저 정보 저장
                    try {
                        userInfo = userRepository.save(userInfo);
                    } catch (Exception exception) {
                        throw new BaseException(DATABASE_ERROR);
                    }
                    // 3. JWT 생성
                    String jwt = jwtService.createJwt(userInfo.getUserIdx());

                    // 4. UserInfoLoginRes로 변환하여 return
                    Integer useridx = userInfo.getUserIdx();
                    return new PostUserRes(useridx, jwt);
                }
                // 1-2. 존재하는 회원이 있다면 로그인
                if (existsUserInfo != null) {
                    // 2. JWT 생성
                    String jwt = jwtService.createJwt(existsUserInfo.getUserIdx());

                    // 3. UserInfoLoginRes로 변환하여 return
                    Integer useridx = existsUserInfo.getUserIdx();
                    return new PostUserRes(useridx, jwt);
                }

            }
        } catch (IOException | BaseException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 회원 정보 수정
     * @param patchUserReq
     * @return PatchUserRes
     * @throws BaseException
     */
    public PatchUserRes updateUser(Integer userIdx, PatchUserReq patchUserReq) throws BaseException {
        //jwt 확인
        User user = userProvider.retrieveUserByUserIdx(jwtService.getUserId());
        if(userIdx != user.getUserIdx()){
            throw new BaseException(FORBIDDEN_USER);
        }

        //유저 정보 수정
        user.setProfilePhoto(patchUserReq.getProfilePhoto());
        user.setUserName(patchUserReq.getUserName());
        user.setEmail(patchUserReq.getEmail());
        user.setPhoneNumber(patchUserReq.getPhoneNumber());
        try {
            user = userRepository.save(user);
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }

        try {
            String socialId = user.getSocialId();
            String profilePhoto = user.getProfilePhoto();
            String userName = user.getUserName();
            String email = user.getEmail();
            String phoneNumber = user.getPhoneNumber();

            return new PatchUserRes(userIdx, socialId, profilePhoto, userName, email, phoneNumber);
        }catch(Exception e){
            throw new BaseException(FAILED_TO_PATCH_USER);
        }
    }

    /**
     * 회원 탈퇴 API
     * @param userIdx
     * @throws BaseException
     */
    public void deleteUser(Integer userIdx) throws BaseException {
        //jwt 확인
        User user = userProvider.retrieveUserByUserIdx(jwtService.getUserId());
        if(userIdx != user.getUserIdx()){
            throw new BaseException(FORBIDDEN_USER);
        }

        user.setStatus("INACTIVE");
        try {
            user = userRepository.save(user);
        } catch (Exception ignored) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

}
