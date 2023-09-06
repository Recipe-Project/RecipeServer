package com.recipe.app.src.recipe.application;

import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson.JacksonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.SearchListResponse;
import com.google.api.services.youtube.model.SearchResult;
import com.recipe.app.src.recipe.application.port.YoutubeRecipeRepository;
import com.recipe.app.src.recipe.domain.YoutubeRecipe;
import com.recipe.app.src.recipe.exception.NotFoundRecipeException;
import com.recipe.app.src.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class YoutubeRecipeService {

    private static final HttpTransport HTTP_TRANSPORT = new NetHttpTransport();
    private static final JsonFactory JSON_FACTORY = new JacksonFactory();
    private static final long NUMBER_OF_VIDEOS_RETURNED = 50;
    private static final String GOOGLE_YOUTUBE_URL = "https://www.youtube.com/watch?v=";
    private static final String YOUTUBE_SEARCH_FIELDS = "items(id/kind,id/videoId,snippet/title,snippet/channelTitle,snippet/thumbnails/default/url,snippet/publishedAt)";

    private final YoutubeRecipeRepository youtubeRecipeRepository;

    @Value("${youtube.api-key}")
    private String youtubeApiKey;

    public List<YoutubeRecipe> getYoutubeRecipes(String keyword) {
        List<YoutubeRecipe> youtubeRecipes = youtubeRecipeRepository.getYoutubeRecipes(keyword);
        if (youtubeRecipes.size() < 10)
            youtubeRecipes = searchYoutubes(keyword);

        return youtubeRecipes;
    }

    public YoutubeRecipe getYoutubeRecipe(Long youtubeRecipeId) {
        return youtubeRecipeRepository.getYoutubeRecipe(youtubeRecipeId).orElseThrow(NotFoundRecipeException::new);
    }

    @Transactional
    public void createYoutubeView(Long youtubeRecipeId, User user) {
        YoutubeRecipe youtubeRecipe = getYoutubeRecipe(youtubeRecipeId);
        youtubeRecipeRepository.saveYoutubeRecipeView(youtubeRecipe, user);
    }

    @Transactional
    public void createYoutubeScrap(Long youtubeRecipeId, User user) {
        YoutubeRecipe youtubeRecipe = getYoutubeRecipe(youtubeRecipeId);
        youtubeRecipeRepository.saveYoutubeRecipeScrap(youtubeRecipe, user);
    }

    @Transactional
    public void deleteYoutubeScrap(Long youtubeRecipeId, User user) {
        YoutubeRecipe youtubeRecipe = getYoutubeRecipe(youtubeRecipeId);
        youtubeRecipeRepository.deleteYoutubeRecipeScrap(youtubeRecipe, user);
    }

    public List<YoutubeRecipe> getScrapYoutubeRecipes(User user) {
        return youtubeRecipeRepository.findYoutubeRecipesByUser(user);
    }

    @Transactional
    public List<YoutubeRecipe> searchYoutubes(String keyword) {

        List<YoutubeRecipe> youtubeRecipes = new ArrayList<>();
        try {
            YouTube youtube = new YouTube.Builder(HTTP_TRANSPORT, JSON_FACTORY, new HttpRequestInitializer() {
                public void initialize(HttpRequest request) throws IOException {
                }
            }).setApplicationName("youtube-cmdline-search-sample").build();

            // Define the API request for retrieving search results.
            YouTube.Search.List search = youtube.search().list("id,snippet");

            search.setKey(youtubeApiKey);
            search.setQ(keyword + " 레시피");
            search.setType("video");
            search.setMaxResults(NUMBER_OF_VIDEOS_RETURNED);
            search.setFields(YOUTUBE_SEARCH_FIELDS);

            SearchListResponse searchResponse = search.execute();
            List<SearchResult> searchResultList = searchResponse.getItems();

            if (searchResultList != null) {
                for (SearchResult rid : searchResultList) {
                    youtubeRecipes.add(YoutubeRecipe.from(rid.getSnippet().getTitle(), rid.getSnippet().getDescription(), rid.getSnippet().getThumbnails().getDefault().getUrl(),
                            LocalDate.ofInstant(Instant.ofEpochMilli(rid.getSnippet().getPublishedAt().getValue()), ZoneId.systemDefault()), rid.getSnippet().getChannelTitle(), rid.getId().getVideoId()));

                }
                return youtubeRecipeRepository.saveYoutubeRecipes(youtubeRecipes);
            }
        } catch (GoogleJsonResponseException e) {
            System.err.println("There was a service error: " + e.getDetails().getCode() + " : "
                    + e.getDetails().getMessage());
        } catch (IOException e) {
            System.err.println("There was an IO error: " + e.getCause() + " : " + e.getMessage());
        } catch (Throwable t) {
            t.printStackTrace();
        }
        return youtubeRecipes;
    }
}
