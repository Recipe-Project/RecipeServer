package com.recipe.app.src.recipe.application.blog;

import com.recipe.app.src.recipe.domain.blog.BlogRecipe;
import com.recipe.app.src.recipe.infra.blog.BlogRecipeRepository;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.net.URL;
import java.util.List;

@Service
public class BlogRecipeThumbnailCrawlingService {

    private final BlogRecipeRepository blogRecipeRepository;

    public BlogRecipeThumbnailCrawlingService(BlogRecipeRepository blogRecipeRepository) {
        this.blogRecipeRepository = blogRecipeRepository;
    }

    @Async
    public void saveThumbnails(List<BlogRecipe> blogRecipes) {

        System.out.println("thumbnail save");

        for (BlogRecipe blogRecipe : blogRecipes) {
            blogRecipe.changeThumbnail(getBlogThumbnailUrl(blogRecipe.getBlogUrl()));
        }

        blogRecipeRepository.saveAll(blogRecipes);
    }

    public String getBlogThumbnailUrl(String blogUrl) {

        if (blogUrl.contains("naver")) {
            return getNaverBlogThumbnailUrl(blogUrl);
        } else if (blogUrl.contains("tistory")) {
            return getTistoryBlogThumbnailUrl(blogUrl);
        } else {
            return "";
        }
    }

    private String getNaverBlogThumbnailUrl(String blogUrl) {

        try {

            URL url = new URL(blogUrl);
            Document doc = Jsoup.parse(url, 5000);

            Elements iframes = doc.select("iframe#mainFrame");
            String src = iframes.attr("src");

            String url2 = "http://blog.naver.com" + src;
            Document doc2 = Jsoup.connect(url2).get();

            return doc2.select("meta[property=og:image]").get(0).attr("content");
        } catch (Exception e) {
            return "";
        }
    }

    private String getTistoryBlogThumbnailUrl(String blogUrl) {

        try {

            Document doc = Jsoup.connect(blogUrl).get();

            Elements imageLinks = doc.getElementsByTag("img");
            String thumbnailUrl = null;
            for (Element image : imageLinks) {
                String temp = image.attr("src");
                if (!temp.contains("admin")) {
                    thumbnailUrl = temp;
                    break;
                }
            }

            return thumbnailUrl;
        } catch (Exception e) {
            return "";
        }
    }
}
