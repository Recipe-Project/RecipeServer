package com.recipe.app.src.recipeInfo;

import com.recipe.app.config.BaseException;
import com.recipe.app.src.fridge.FridgeRepository;
import com.recipe.app.src.fridge.models.Fridge;
import com.recipe.app.src.recipeInfo.models.*;
import com.recipe.app.src.recipeIngredient.models.RecipeIngredient;
import com.recipe.app.src.recipeProcess.models.RecipeProcess;
import com.recipe.app.src.scrapBlog.ScrapBlogRepository;
import com.recipe.app.src.scrapPublic.ScrapPublicRepository;
import com.recipe.app.src.user.UserProvider;
import com.recipe.app.src.user.models.User;
import com.recipe.app.utils.JwtService;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.recipe.app.config.BaseResponseStatus.*;
import static com.recipe.app.config.secret.Secret.NAVER_CLIENT_ID;
import static com.recipe.app.config.secret.Secret.NAVER_CLINET_SECRET;
import static com.sun.el.util.MessageFactory.get;

@Service
public class RecipeInfoProvider {
    private final FridgeRepository fridgeRepository;
    private final ScrapBlogRepository scrapBlogRepository;
    private final ScrapPublicRepository scrapPublicRepository;
    private final UserProvider userProvider;
    private final RecipeInfoRepository recipeInfoRepository;
    private final JwtService jwtService;

    @Autowired
    public RecipeInfoProvider(FridgeRepository fridgeRepository, ScrapBlogRepository scrapBlogRepository, ScrapPublicRepository scrapPublicRepository, UserProvider userProvider, RecipeInfoRepository recipeInfoRepository, JwtService jwtService) {
        this.fridgeRepository = fridgeRepository;
        this.scrapBlogRepository = scrapBlogRepository;
        this.scrapPublicRepository = scrapPublicRepository;
        this.userProvider = userProvider;
        this.recipeInfoRepository = recipeInfoRepository;
        this.jwtService = jwtService;
    }

    /**
     * Idx로 레시피 조회
     *
     * @param recipeId
     * @return User
     * @throws BaseException
     */
    public RecipeInfo retrieveRecipeByRecipeId(Integer recipeId) throws BaseException {
        RecipeInfo recipeInfo;
        try {
            recipeInfo = recipeInfoRepository.findById(recipeId).orElse(null);
        } catch (Exception ignored) {
            throw new BaseException(FAILE_TO_GET_RECIPE_INFO);
        }

        if (recipeInfo == null || !recipeInfo.getStatus().equals("ACTIVE")) {
            throw new BaseException(NOT_FOUND_RECIPE_INFO);
        }

        return recipeInfo;
    }

    /**
     * 레시피 검색
     * @param jwtUserIdx, keyword
     * @return List<GetRecipeInfosRes>
     * @throws BaseException
     */

    public List<GetRecipeInfosRes> retrieveRecipeInfos(Integer jwtUserIdx, String keyword) throws BaseException {
        User user = userProvider.retrieveUserByUserIdx(jwtUserIdx);
        List<RecipeInfo> recipeInfoList;
        try {
            recipeInfoList= recipeInfoRepository.searchRecipeInfos(keyword, "ACTIVE");
        } catch (Exception ignored) {
            throw new BaseException(DATABASE_ERROR);
        }

        List<GetRecipeInfosRes> getRecipeInfosResList = new ArrayList<>();
        for(int i=0;i<recipeInfoList.size();i++){
            RecipeInfo recipeInfo = recipeInfoList.get(i);
            Integer recipeId = recipeInfo.getRecipeId();
            String title = recipeInfo.getRecipeNmKo();
            String description = recipeInfo.getSumry();
            String thumbnail = recipeInfo.getImgUrl();
            String userScrapYN = "N";
            for(int j=0;j<user.getScrapPublics().size();j++){
                if(user.getScrapPublics().get(j).getRecipeInfo().getRecipeId()==recipeId && user.getScrapPublics().get(j).getStatus().equals("ACTIVE")){
                    userScrapYN = "Y";
                }
            }

            Integer userScrapCnt = 0;
            try{
                userScrapCnt = Math.toIntExact(scrapPublicRepository.countByRecipeInfoAndStatus(recipeInfo, "ACTIVE"));
            }catch (Exception e){
                throw new BaseException(DATABASE_ERROR);
            }

            getRecipeInfosResList.add(new GetRecipeInfosRes(recipeId, title, description, thumbnail, userScrapYN, userScrapCnt));

        }

        return getRecipeInfosResList;

    }


    /**
     * 레시피 검색 상세 조회
     * @param jwtUserIdx, recipeIdx
     * @return GetRecipeInfoRes
     * @throws BaseException
     */

    public GetRecipeInfoRes retrieveRecipeInfo(Integer jwtUserIdx, Integer recipeIdx) throws BaseException {
        User user = userProvider.retrieveUserByUserIdx(jwtUserIdx);

        RecipeInfo recipeInfo= retrieveRecipeByRecipeId(recipeIdx);

        String recipeName= recipeInfo.getRecipeNmKo();
        String summary = recipeInfo.getSumry();
        String thumbnail = recipeInfo.getImgUrl();
        String cookingTime = recipeInfo.getCookingTime();
        String level = recipeInfo.getLevelNm();
        System.out.println(recipeInfo.getRecipeId());

        List<Fridge> fridges = fridgeRepository.findByUserAndStatus(user, "ACTIVE");
        List<RecipeIngredient> recipeIngredients = recipeInfo.getRecipeIngredients();
        List<RecipeIngredientList> recipeIngredientList = new ArrayList<>();
        for(int i=0;i<recipeIngredients.size();i++){
            RecipeIngredient ingredient = recipeIngredients.get(i);
            Integer recipeIngredientIdx = ingredient.getIdx();
            String recipeIngredientName = ingredient.getIrdntNm();
            String recipeIngredientCpcty = ingredient.getIrdntCpcty();
            String inFridgeYN = "N";
            for(int j=0;j<fridges.size();j++){
                if(recipeIngredientName.equals(fridges.get(j).getIngredientName())){
                    inFridgeYN="Y";
                    break;
                }
            }
            recipeIngredientList.add(new RecipeIngredientList(recipeIngredientIdx, recipeIngredientName, recipeIngredientCpcty, inFridgeYN));
        }
        List<RecipeProcess> recipeProcesses = recipeInfo.getRecipeProcesses();
        List<RecipeProcessList> recipeProcessList = new ArrayList<>();
        for(int i=0;i<recipeProcesses.size();i++){
            RecipeProcess process = recipeProcesses.get(i);
            Integer recipeProcessIdx = process.getIdx();
            Integer recipeProcessNo = process.getCookingNo();
            String recipeProcessDc = process.getCookingDc();
            String recipeProcessImg = process.getStreStepImageUrl();
            recipeProcessList.add(new RecipeProcessList(recipeProcessIdx, recipeProcessNo, recipeProcessDc, recipeProcessImg));
        }

        String userScrapYN = "N";
        for(int j=0;j<user.getScrapPublics().size();j++){
            if(user.getScrapPublics().get(j).getRecipeInfo().getRecipeId()==recipeIdx && user.getScrapPublics().get(j).getStatus().equals("ACTIVE")){
                userScrapYN = "Y";
            }
        }

        Integer userScrapCnt = 0;
        try{
            userScrapCnt = Math.toIntExact(scrapPublicRepository.countByRecipeInfoAndStatus(recipeInfo, "ACTIVE"));
        }catch (Exception e){
            throw new BaseException(DATABASE_ERROR);
        }

        return new GetRecipeInfoRes(recipeIdx, recipeName, summary, thumbnail, cookingTime, level, recipeIngredientList, recipeProcessList, userScrapYN, userScrapCnt);
    }


    /**
     * 블로그 검색
     * @param jwtUserIdx, keyword
     * @return List<GetRecipeBlogsRes>
     * @throws BaseException
     */

    public GetRecipeBlogsRes retrieveRecipeBlogs(Integer jwtUserIdx, String keyword, Integer display, Integer start) throws BaseException {
        User user = userProvider.retrieveUserByUserIdx(jwtUserIdx);

        JSONObject jsonObject;
        String text;
        try {
            text = URLEncoder.encode(keyword+" 레시피", "UTF-8");
        }catch(Exception e){
            throw new BaseException(FAILED_TO_URL_ENCODER);
        }
        String apiURL = "https://openapi.naver.com/v1/search/blog?sort=sim&query="+text+"&display="+display+"&start="+start;

        Map<String, String> requestHeaders = new HashMap<>();
        requestHeaders.put("X-Naver-Client-Id", NAVER_CLIENT_ID);
        requestHeaders.put("X-Naver-Client-Secret", NAVER_CLINET_SECRET);

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


        Integer total;
        JSONArray arr;
        try{
            JSONParser jsonParser = new JSONParser();
            jsonObject = (JSONObject) jsonParser.parse(body);
            arr = (JSONArray) jsonObject.get("items");
            total = Integer.parseInt(jsonObject.get("total").toString());
            System.out.println(total);
        }
        catch (Exception e){
            throw new BaseException(FAILED_TO_PARSE);
        }

        List<BlogList> blogList = new ArrayList<>();

        for(int i=0;i<arr.size();i++){
            String title=null;
            String blogUrl=null;
            String description=null;
            String bloggerName=null;
            String postDate = null;
            String thumbnail=null;

            JSONObject tmp = (JSONObject) arr.get(i);
            title = tmp.get("title").toString();
            title = title.replaceAll("<(/)?([a-zA-Z]*)(\\s[a-zA-Z]*=[^>]*)?(\\s)*(/)?>", "");
            blogUrl = tmp.get("link").toString();
            description = tmp.get("description").toString();
            description = description.replaceAll("<(/)?([a-zA-Z]*)(\\s[a-zA-Z]*=[^>]*)?(\\s)*(/)?>", "");
            bloggerName = tmp.get("bloggername").toString();
            postDate = tmp.get("postdate").toString();
            //System.out.println(blogUrl);

            if(blogUrl.contains("naver")) {
                try {
                    URL url = new URL(blogUrl);
                    Document doc = Jsoup.parse(url, 5000);

                    String src = doc.getElementById("mainFrame").toString().replace("&amp;", "&");
                    //System.out.println(src);

                    int s = src.indexOf("src=") + 5;
                    int e = src.indexOf("&from=");
                    src = src.substring(s, e);
                    src = "http://blog.naver.com" + src;
                    s = src.indexOf("logNo=") + 6;
                    String logNo = src.substring(s);

                    doc = Jsoup.connect(src).get();


                    // 본문에서 img태그를 모두 파싱한 후 "_photoImage" class 속성을 포함한 이미지의 URL을 파싱
                    Elements imageLinks = doc.getElementById("post-view" + logNo).getElementsByTag("img");
                    String result = null;
                    for (Element image : imageLinks) {
                        String temp = image.attr("src");
                        if (temp.contains("postfiles")) {
                            result = temp;
                            result = result.replace("w80_blur", "w966");
                            break;
                        }
                    }

                    thumbnail = result;

                } catch (Exception e) {
                    throw new BaseException(FAILED_TO_CRAWLING);
                }
            }
            else if(blogUrl.contains("tistory")){
                try {
                    Document doc = Jsoup.connect(blogUrl).get();

                    Elements imageLinks = doc.getElementsByTag("img");
                    String result = null;
                    for (Element image : imageLinks) {
                        String temp = image.attr("src");
                        System.out.println(temp);

                        if (!temp.contains("admin")) {
                            result = temp;
                            break;
                        }
                    }

                    thumbnail = result;

                } catch (Exception e) {
                    throw new BaseException(FAILED_TO_CRAWLING);
                }
            }

            String userScrapYN = "N";
            for(int j=0;j<user.getScrapBlogs().size();j++){
                if(user.getScrapBlogs().get(j).getBlogUrl().equals(blogUrl) && user.getScrapBlogs().get(j).getStatus().equals("ACTIVE")){
                    userScrapYN = "Y";
                }
            }

            Integer userScrapCnt = 0;
            try{
                userScrapCnt = Math.toIntExact(scrapBlogRepository.countByBlogUrlAndStatus(blogUrl, "ACTIVE"));
            }catch (Exception e){
                throw new BaseException(DATABASE_ERROR);
            }

            blogList.add(new BlogList(title, blogUrl, description, bloggerName, postDate, thumbnail, userScrapYN, userScrapCnt));
        }


        return new GetRecipeBlogsRes(total, blogList);

    }
}