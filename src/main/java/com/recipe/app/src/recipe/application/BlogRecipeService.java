package com.recipe.app.src.recipe.application;

import com.recipe.app.common.exception.BaseException;
import com.recipe.app.src.recipe.application.port.BlogRecipeRepository;
import com.recipe.app.src.recipe.domain.BlogRecipe;
import com.recipe.app.src.recipe.exception.NotFoundRecipeException;
import com.recipe.app.src.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.recipe.app.common.response.BaseResponseStatus.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BlogRecipeService {

    private final BlogRecipeRepository blogRecipeRepository;

    @Value("${naver.client-id}")
    private String naverClientId;
    @Value("${naver.client-secret}")
    private String naverClientSecret;

    public List<BlogRecipe> getBlogRecipes(String keyword) {
        List<BlogRecipe> blogRecipes = blogRecipeRepository.getBlogRecipes(keyword);
        if (blogRecipes.size() < 10)
            blogRecipes = searchNaverBlogRecipes(keyword);

        return blogRecipes;
    }

    public BlogRecipe getBlogRecipe(Long blogRecipeId) {
        return blogRecipeRepository.getBlogRecipe(blogRecipeId).orElseThrow(NotFoundRecipeException::new);
    }

    @Transactional
    public void createBlogView(Long blogRecipeId, User user) {
        BlogRecipe blogRecipe = getBlogRecipe(blogRecipeId);
        blogRecipeRepository.saveBlogRecipeView(blogRecipe, user);
    }

    @Transactional
    public void createBlogRecipeScrap(Long blogRecipeId, User user) {
        BlogRecipe blogRecipe = getBlogRecipe(blogRecipeId);
        blogRecipeRepository.saveBlogRecipeScrap(blogRecipe, user);
    }

    @Transactional
    public void deleteBlogRecipeScrap(Long blogRecipeId, User user) {
        BlogRecipe blogRecipe = getBlogRecipe(blogRecipeId);
        blogRecipeRepository.deleteBlogRecipeScrap(blogRecipe, user);
    }

    public List<BlogRecipe> getScrapBlogRecipes(User user) {
        return blogRecipeRepository.findBlogRecipesByUser(user);
    }

    @Transactional
    public List<BlogRecipe> searchNaverBlogRecipes(String keyword) {
        JSONObject jsonObject;
        String text;
        try {
            text = URLEncoder.encode(keyword + " 레시피", "UTF-8");
        } catch (Exception e) {
            throw new BaseException(FAILED_TO_URL_ENCODER);
        }

        String apiURL = "https://openapi.naver.com/v1/search/blog?sort=sim&query=" + text;

        Map<String, String> requestHeaders = new HashMap<>();
        requestHeaders.put("X-Naver-Client-Id", naverClientId);
        requestHeaders.put("X-Naver-Client-Secret", naverClientSecret);

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

        int total = 0;
        JSONArray arr;
        try {
            JSONParser jsonParser = new JSONParser();
            jsonObject = (JSONObject) jsonParser.parse(body);
            arr = (JSONArray) jsonObject.get("items");
            total = Integer.parseInt(jsonObject.get("total").toString());
        } catch (Exception e) {
            throw new BaseException(FAILED_TO_PARSE);
        }

        List<BlogRecipe> blogs = new ArrayList<>();
        for (int i = 0; i < arr.size(); i++) {
            JSONObject tmp = (JSONObject) arr.get(i);
            String title = tmp.get("title").toString();
            title = title.replaceAll("<(/)?([a-zA-Z]*)(\\s[a-zA-Z]*=[^>]*)?(\\s)*(/)?>", "");
            String blogUrl = tmp.get("link").toString();
            String description = tmp.get("description").toString();
            description = description.replaceAll("<(/)?([a-zA-Z]*)(\\s[a-zA-Z]*=[^>]*)?(\\s)*(/)?>", "");
            String bloggerName = tmp.get("bloggername").toString();
            LocalDate postDate = LocalDate.parse(tmp.get("postdate").toString(), DateTimeFormatter.ofPattern("yyyyMMdd"));
            String thumbnail = null;
            if (blogUrl.contains("naver")) {
                try {
                    URL url = new URL(blogUrl);
                    Document doc = Jsoup.parse(url, 5000);

                    Elements iframes = doc.select("iframe#mainFrame");
                    String src = iframes.attr("src");

                    String url2 = "http://blog.naver.com" + src;
                    Document doc2 = Jsoup.connect(url2).get();

                    String[] blog_logNo = src.split("&");
                    String[] logNo_split = blog_logNo[1].split("=");
                    String logNo = logNo_split[1];

                    // 블로그 썸네일 가져오기
                    thumbnail = doc2.select("meta[property=og:image]").get(0).attr("content");

                } catch (Exception ignored) {
                }
            } else if (blogUrl.contains("tistory")) {
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

                } catch (Exception ignored) {
                }
            }

            blogs.add(BlogRecipe.from(blogUrl, thumbnail, title, description, postDate, bloggerName));
        }

        return blogRecipeRepository.saveBlogRecipes(blogs);
    }

    public long countBlogScrapByUser(User user) {
        return blogRecipeRepository.countBlogScrapByUser(user);
    }
}
