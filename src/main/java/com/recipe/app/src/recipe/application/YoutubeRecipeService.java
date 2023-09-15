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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

    public Page<YoutubeRecipe> getYoutubeRecipes(String keyword, int page, int size, String sort) {

        Pageable pageable = PageRequest.of(page, size);
        Page<YoutubeRecipe> youtubeRecipes;
        if (sort.equals("youtubeScraps"))
            youtubeRecipes = youtubeRecipeRepository.getYoutubeRecipesOrderByYoutubeScrapSizeDesc(keyword, pageable);
        else if (sort.equals("youtubeViews"))
            youtubeRecipes = youtubeRecipeRepository.getYoutubeRecipesOrderByYoutubeViewSizeDesc(keyword, pageable);
        else
            youtubeRecipes = youtubeRecipeRepository.getYoutubeRecipesOrderByCreatedAtDesc(keyword, pageable);

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

    public Page<YoutubeRecipe> getScrapYoutubeRecipes(User user, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return youtubeRecipeRepository.findYoutubeRecipesByUser(user, pageable);
    }

    public long countYoutubeScrapByUser(User user) {
        return youtubeRecipeRepository.countYoutubeScrapByUser(user);
    }

    @Transactional
    public Page<YoutubeRecipe> searchYoutubes(String keyword, int page, int size, String sort) {

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
                List<YoutubeRecipe> youtubes = new ArrayList<>();
                for (SearchResult rid : searchResultList) {
                    youtubes.add(YoutubeRecipe.from(rid.getSnippet().getTitle(), rid.getSnippet().getDescription(), rid.getSnippet().getThumbnails().getDefault().getUrl(),
                            LocalDate.ofInstant(Instant.ofEpochMilli(rid.getSnippet().getPublishedAt().getValue()), ZoneId.systemDefault()), rid.getSnippet().getChannelTitle(), rid.getId().getVideoId()));
                }
                List<String> youtubeIds = youtubes.stream().map(YoutubeRecipe::getYoutubeId).collect(Collectors.toList());
                List<YoutubeRecipe> existYoutubeRecipes = youtubeRecipeRepository.findYoutubeRecipesByYoutubeIdIn(youtubeIds);
                Map<String, YoutubeRecipe> existYoutubeRecipesMapByYoutubeId = existYoutubeRecipes.stream().collect(Collectors.toMap(YoutubeRecipe::getYoutubeId, v -> v));
                List<YoutubeRecipe> youtubeRecipes = youtubes.stream()
                        .map(youtubeRecipe -> {
                            if (existYoutubeRecipesMapByYoutubeId.containsKey(youtubeRecipe.getYoutubeId())) {
                                return existYoutubeRecipesMapByYoutubeId.get(youtubeRecipe.getYoutubeId());
                            }
                            return youtubeRecipe;
                        })
                        .collect(Collectors.toList());

                youtubeRecipeRepository.saveYoutubeRecipes(youtubeRecipes);
            }
        } catch (GoogleJsonResponseException e) {
            System.err.println("There was a service error: " + e.getDetails().getCode() + " : "
                    + e.getDetails().getMessage());
        } catch (IOException e) {
            System.err.println("There was an IO error: " + e.getCause() + " : " + e.getMessage());
        } catch (Throwable t) {
            t.printStackTrace();
        }

        return getYoutubeRecipes(keyword, page, size, sort);
    }
}
