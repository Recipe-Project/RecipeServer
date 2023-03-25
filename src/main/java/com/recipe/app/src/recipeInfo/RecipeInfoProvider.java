package com.recipe.app.src.recipeInfo;

import com.recipe.app.config.BaseException;
import com.recipe.app.src.fridge.FridgeRepository;
import com.recipe.app.src.fridge.models.Fridge;
import com.recipe.app.src.ingredient.IngredientRepository;
import com.recipe.app.src.ingredient.models.Ingredient;
import com.recipe.app.src.recipeInfo.models.*;
import com.recipe.app.src.recipeIngredient.models.RecipeIngredient;
import com.recipe.app.src.recipeKeyword.RecipeKeywordRepository;
import com.recipe.app.src.recipeKeyword.models.RecipeKeyword;
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
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

import static com.recipe.app.config.BaseResponseStatus.*;
import static com.recipe.app.config.secret.Secret.NAVER_CLIENT_ID;
import static com.recipe.app.config.secret.Secret.NAVER_CLINET_SECRET;
import static com.sun.el.util.MessageFactory.get;

@Service
public class RecipeInfoProvider {
    private final IngredientRepository ingredientRepository;
    private final RecipeKeywordRepository recipeKeywordRepository;
    private final FridgeRepository fridgeRepository;
    private final ScrapBlogRepository scrapBlogRepository;
    private final ScrapPublicRepository scrapPublicRepository;
    private final UserProvider userProvider;
    private final RecipeInfoRepository recipeInfoRepository;
    private final JwtService jwtService;

    @Autowired
    public RecipeInfoProvider(IngredientRepository ingredientRepository, RecipeKeywordRepository recipeKeywordRepository, FridgeRepository fridgeRepository, ScrapBlogRepository scrapBlogRepository, ScrapPublicRepository scrapPublicRepository, UserProvider userProvider, RecipeInfoRepository recipeInfoRepository, JwtService jwtService) {
        this.ingredientRepository = ingredientRepository;
        this.recipeKeywordRepository = recipeKeywordRepository;
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

        RecipeKeyword recipeKeyword = new RecipeKeyword(keyword);
        try{
            recipeKeyword = recipeKeywordRepository.save(recipeKeyword);
        }catch (Exception e){
            throw new BaseException(DATABASE_ERROR);
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
        List<Ingredient> ingredientList = ingredientRepository.findByStatus("ACTIVE");
        List<Fridge> fridges = fridgeRepository.findByUserAndStatus(user, "ACTIVE");
        List<RecipeIngredient> recipeIngredients = recipeInfo.getRecipeIngredients();
        List<RecipeIngredientList> recipeIngredientList = new ArrayList<>();

        Collections.sort(ingredientList, new Comparator<Ingredient>(){
            @Override
            public int compare(Ingredient o1, Ingredient o2){
                if(o1.getName().length()<o2.getName().length()){
                    return -1;
                }
                else if (o1.getName().length()>o2.getName().length()){
                    return 1;
                }
                else {
                    return 0;
                }
            }
        });

        Collections.sort(fridges, new Comparator<Fridge>(){
            @Override
            public int compare(Fridge o1, Fridge o2){
                if(o1.getIngredientName().length()<o2.getIngredientName().length()){
                    return 1;
                }
                else if (o1.getIngredientName().length()>o2.getIngredientName().length()){
                    return -1;
                }
                else {
                    return 0;
                }
            }
        });

        for(int i=0;i<recipeIngredients.size();i++){
            RecipeIngredient ingredient = recipeIngredients.get(i);
            Integer recipeIngredientIdx = ingredient.getIdx();
            String recipeIngredientName = ingredient.getIrdntNm();
            String recipeIngredientCpcty = ingredient.getIrdntCpcty();
            String recipeIngredientIcon = null;
            for(int j=0;j<ingredientList.size();j++){
                if(recipeIngredientName.contains(ingredientList.get(j).getName())){
                    recipeIngredientIcon = ingredientList.get(j).getIcon();
                }
            }

            if(recipeIngredientName.contains("대하")){
                Ingredient igr;
                try{
                    igr = ingredientRepository.findByNameAndStatus("새우", "ACTIVE");
                }catch(Exception e){
                    throw new BaseException(DATABASE_ERROR);
                }
                if(igr!=null)
                    recipeIngredientIcon = igr.getIcon();
            }
            else if(recipeIngredientName.contains("달걀")){
                Ingredient igr;
                try{
                    igr = ingredientRepository.findByNameAndStatus("계란", "ACTIVE");
                }catch(Exception e){
                    throw new BaseException(DATABASE_ERROR);
                }
                if(igr!=null)
                    recipeIngredientIcon = igr.getIcon();
            }
            else if(recipeIngredientName.contains("쇠고기")){
                Ingredient igr;
                try{
                    igr = ingredientRepository.findByNameAndStatus("소고기", "ACTIVE");
                }catch(Exception e){
                    throw new BaseException(DATABASE_ERROR);
                }
                if(igr!=null)
                    recipeIngredientIcon = igr.getIcon();
            }
            else if(recipeIngredientName.contains("후춧가루")){
                Ingredient igr;
                try{
                    igr = ingredientRepository.findByNameAndStatus("후추", "ACTIVE");
                }catch(Exception e){
                    throw new BaseException(DATABASE_ERROR);
                }
                if(igr!=null)
                    recipeIngredientIcon = igr.getIcon();
            }
            else if(recipeIngredientName.contains("다진마늘")){
                Ingredient igr;
                try{
                    igr = ingredientRepository.findByNameAndStatus("간마늘", "ACTIVE");
                }catch(Exception e){
                    throw new BaseException(DATABASE_ERROR);
                }
                if(igr!=null)
                    recipeIngredientIcon = igr.getIcon();
            }
            else if(recipeIngredientName.equals("어린잎채소")||recipeIngredientName.equals("무순")){
                Ingredient igr;
                try{
                    igr = ingredientRepository.findByNameAndStatus("새싹채소", "ACTIVE");
                }catch(Exception e){
                    throw new BaseException(DATABASE_ERROR);
                }
                if(igr!=null)
                    recipeIngredientIcon = igr.getIcon();
            }
            else if(recipeIngredientName.equals("조갯살")||recipeIngredientName.contains("바지락")||recipeIngredientName.contains("전복")||recipeIngredientName.contains("굴")||recipeIngredientName.contains("가리비")){
                Ingredient igr;
                try{
                    igr = ingredientRepository.findByNameAndStatus("조개", "ACTIVE");
                }catch(Exception e){
                    throw new BaseException(DATABASE_ERROR);
                }
                if(igr!=null)
                    recipeIngredientIcon = igr.getIcon();
            }
            else if(recipeIngredientName.contains("케첩")||recipeIngredientName.contains("케찹")){
                Ingredient igr;
                try{
                    igr = ingredientRepository.findByNameAndStatus("케찹", "ACTIVE");
                }catch(Exception e){
                    throw new BaseException(DATABASE_ERROR);
                }
                if(igr!=null)
                    recipeIngredientIcon = igr.getIcon();
            }
            else if(recipeIngredientName.contains("돼지")){
                Ingredient igr;
                try{
                    igr = ingredientRepository.findByNameAndStatus("돼지고기", "ACTIVE");
                }catch(Exception e){
                    throw new BaseException(DATABASE_ERROR);
                }
                if(igr!=null)
                    recipeIngredientIcon = igr.getIcon();
            }
            else if(recipeIngredientIcon==null&&recipeIngredientName.contains("닭")&&!recipeIngredientName.equals("닭발")){
                Ingredient igr;
                try{
                    igr = ingredientRepository.findByNameAndStatus("닭고기", "ACTIVE");
                }catch(Exception e){
                    throw new BaseException(DATABASE_ERROR);
                }
                if(igr!=null)
                    recipeIngredientIcon = igr.getIcon();
            }
            else if(recipeIngredientName.contains("연어")||recipeIngredientName.contains("북어")||recipeIngredientName.contains("대구")||recipeIngredientName.contains("동태")||recipeIngredientName.contains("광어")||recipeIngredientName.contains("코다리")||recipeIngredientName.contains("아귀")||recipeIngredientName.contains("아구")||recipeIngredientName.contains("조기")) {
                Ingredient igr;
                try {
                    igr = ingredientRepository.findByNameAndStatus("생선", "ACTIVE");
                } catch (Exception e) {
                    throw new BaseException(DATABASE_ERROR);
                }
                if (igr != null)
                    recipeIngredientIcon = igr.getIcon();
            }
            else if(recipeIngredientName.equals("소면")){
                Ingredient igr;
                try{
                    igr = ingredientRepository.findByNameAndStatus("국수", "ACTIVE");
                }catch(Exception e){
                    throw new BaseException(DATABASE_ERROR);
                }
                if(igr!=null)
                    recipeIngredientIcon = igr.getIcon();
            }
            else if(recipeIngredientName.equals("김칫잎")){
                Ingredient igr;
                try{
                    igr = ingredientRepository.findByNameAndStatus("김치", "ACTIVE");
                }catch(Exception e){
                    throw new BaseException(DATABASE_ERROR);
                }
                if(igr!=null)
                    recipeIngredientIcon = igr.getIcon();
            }
            else if(recipeIngredientName.equals("인절미")){
                Ingredient igr;
                try{
                    igr = ingredientRepository.findByNameAndStatus("떡", "ACTIVE");
                }catch(Exception e){
                    throw new BaseException(DATABASE_ERROR);
                }
                if(igr!=null)
                    recipeIngredientIcon = igr.getIcon();
            }
            else if(recipeIngredientName.equals("고추가루")){
                Ingredient igr;
                try{
                    igr = ingredientRepository.findByNameAndStatus("고춧가루", "ACTIVE");
                }catch(Exception e){
                    throw new BaseException(DATABASE_ERROR);
                }
                if(igr!=null)
                    recipeIngredientIcon = igr.getIcon();
            }
            else if(recipeIngredientName.equals("올리브오일")){
                Ingredient igr;
                try{
                    igr = ingredientRepository.findByNameAndStatus("올리브유", "ACTIVE");
                }catch(Exception e){
                    throw new BaseException(DATABASE_ERROR);
                }
                if(igr!=null)
                    recipeIngredientIcon = igr.getIcon();
            }
            else if(recipeIngredientName.contains("양송이")){
                Ingredient igr;
                try{
                    igr = ingredientRepository.findByNameAndStatus("버섯", "ACTIVE");
                }catch(Exception e){
                    throw new BaseException(DATABASE_ERROR);
                }
                if(igr!=null)
                    recipeIngredientIcon = igr.getIcon();
            }
            else if(recipeIngredientName.contains("스파게티")){
                Ingredient igr;
                try{
                    igr = ingredientRepository.findByNameAndStatus("파스타", "ACTIVE");
                }catch(Exception e){
                    throw new BaseException(DATABASE_ERROR);
                }
                if(igr!=null)
                    recipeIngredientIcon = igr.getIcon();
            }
            else if(recipeIngredientName.contains("맛살")){
                Ingredient igr;
                try{
                    igr = ingredientRepository.findByNameAndStatus("게", "ACTIVE");
                }catch(Exception e){
                    throw new BaseException(DATABASE_ERROR);
                }
                if(igr!=null)
                    recipeIngredientIcon = igr.getIcon();
            }
            else if(recipeIngredientName.contains("포도씨유")){
                Ingredient igr;
                try{
                    igr = ingredientRepository.findByNameAndStatus("식용유", "ACTIVE");
                }catch(Exception e){
                    throw new BaseException(DATABASE_ERROR);
                }
                if(igr!=null)
                    recipeIngredientIcon = igr.getIcon();
            }
            if(recipeIngredientName.contains("스톡")||recipeIngredientName.contains("국물")||recipeIngredientName.contains("다시물")|| recipeIngredientName.contains("육수")||recipeIngredientName.equals("우무")||recipeIngredientName.contains("알")||recipeIngredientName.contains("고추냉이")||recipeIngredientName.equals("마늘종")){
                recipeIngredientIcon=null;
            }


            String inFridgeYN = "N";
            for(int j=0;j<fridges.size();j++){
                if(recipeIngredientName.contains(fridges.get(j).getIngredientName()) && recipeIngredientIcon!=null &&recipeIngredientIcon.equals(fridges.get(j).getIngredientIcon())){
                    inFridgeYN="Y";
                    break;
                }
            }

            recipeIngredientList.add(new RecipeIngredientList(recipeIngredientIdx, recipeIngredientName, recipeIngredientIcon, recipeIngredientCpcty, inFridgeYN));
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
        String apiURL = "https://openapi.naver.com/v1/search/blog?sort=sim&query="+text+"&display=25&start=1";

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

        Integer total;
        JSONArray arr;
        try{
            JSONParser jsonParser = new JSONParser();
            jsonObject = (JSONObject) jsonParser.parse(body);
            arr = (JSONArray) jsonObject.get("items");
            total = Integer.parseInt(jsonObject.get("total").toString());
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
            SimpleDateFormat datetime = new SimpleDateFormat("yyyyMMdd", Locale.KOREA);
            datetime.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));
            Date date;
            try {
                date = datetime.parse(postDate);
            }catch (Exception e){
                throw new BaseException(DATE_PARSE_ERROR);
            }
            SimpleDateFormat datetime2 = new SimpleDateFormat("yyyy.M.d", Locale.KOREA);
            datetime2.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));
            String postdate = datetime2.format(date);

            if(blogUrl.contains("naver")) {
                try {
                    URL url = new URL(blogUrl);
                    Document doc = Jsoup.parse(url, 5000);

                    Elements iframes = doc.select("iframe#mainFrame");
                    String src = iframes.attr("src");

                    String url2 = "http://blog.naver.com"+ src;
                    Document doc2 = Jsoup.connect(url2).get();

                    String[] blog_logNo = src.split("&");
                    String[] logNo_split = blog_logNo[1].split("=");
                    String logNo = logNo_split[1];

                    // 블로그 썸네일 가져오기
                    thumbnail = doc2.select("meta[property=og:image]").get(0).attr("content");

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

            blogList.add(new BlogList(title, blogUrl, description, bloggerName, postdate, thumbnail, userScrapYN, userScrapCnt));
        }

        RecipeKeyword recipeKeyword = new RecipeKeyword(keyword);
        try{
            recipeKeyword = recipeKeywordRepository.save(recipeKeyword);
        }catch (Exception e){
            throw new BaseException(DATABASE_ERROR);
        }

        return new GetRecipeBlogsRes(total, blogList);

    }
}