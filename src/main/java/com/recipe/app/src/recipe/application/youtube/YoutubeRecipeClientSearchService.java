package com.recipe.app.src.recipe.application.youtube;

import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson.JacksonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.SearchListResponse;
import com.google.api.services.youtube.model.SearchResult;
import com.recipe.app.src.recipe.domain.youtube.YoutubeRecipe;
import com.recipe.app.src.recipe.infra.youtube.YoutubeRecipeRepository;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
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

@Slf4j
@Service
public class YoutubeRecipeClientSearchService {

    private static final HttpTransport HTTP_TRANSPORT = new NetHttpTransport();
    private static final JsonFactory JSON_FACTORY = new JacksonFactory();
    private static final String YOUTUBE_SEARCH_FIELDS = "items(id/kind,id/videoId,snippet/title,snippet/channelTitle,snippet/thumbnails/default/url,snippet/publishedAt,snippet/description)";
    private static final long NUMBER_OF_VIDEOS_RETURNED = 50;
    @Value("${google.api-key}")
    private String youtubeApiKey;

    private final YoutubeRecipeRepository youtubeRecipeRepository;

    public YoutubeRecipeClientSearchService(YoutubeRecipeRepository youtubeRecipeRepository) {
        this.youtubeRecipeRepository = youtubeRecipeRepository;
    }

    @CircuitBreaker(name = "recipe-youtube-search", fallbackMethod = "fallback")
    public List<YoutubeRecipe> searchYoutube(String keyword, int size) throws IOException {

        log.info("youtube search api call");

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

        List<YoutubeRecipe> youtubeRecipes = new ArrayList<>();
        if (searchResultList != null) {
            for (SearchResult rid : searchResultList) {
                youtubeRecipes.add(YoutubeRecipe.builder()
                        .title(rid.getSnippet().getTitle())
                        .description(rid.getSnippet().getDescription())
                        .thumbnailImgUrl(rid.getSnippet().getThumbnails().getDefault().getUrl())
                        .postDate(LocalDate.ofInstant(Instant.ofEpochMilli(rid.getSnippet().getPublishedAt().getValue()), ZoneId.systemDefault()))
                        .channelName(rid.getSnippet().getChannelTitle())
                        .youtubeId(rid.getId().getVideoId())
                        .build());
            }
        }

        createYoutubeRecipes(youtubeRecipes);

        return youtubeRecipes.subList(0, size);
    }

    public List<YoutubeRecipe> fallback(String keyword, int size, Exception e) {

        log.info("fallback call - " + e.getMessage());

        return youtubeRecipeRepository.findByKeywordLimit(keyword, size);
    }

    @Transactional
    public void createYoutubeRecipes(List<YoutubeRecipe> youtubeRecipes) {

        List<String> youtubeIds = youtubeRecipes.stream().map(YoutubeRecipe::getYoutubeId).collect(Collectors.toList());
        List<YoutubeRecipe> existYoutubeRecipes = youtubeRecipeRepository.findByYoutubeIdIn(youtubeIds);
        Map<String, YoutubeRecipe> existYoutubeRecipesMapByYoutubeId = existYoutubeRecipes.stream().collect(Collectors.toMap(YoutubeRecipe::getYoutubeId, v -> v));

        youtubeRecipeRepository.saveAll(youtubeRecipes.stream()
                .filter(youtubeRecipe -> !existYoutubeRecipesMapByYoutubeId.containsKey(youtubeRecipe.getYoutubeId()))
                .collect(Collectors.toList()));
    }
}
