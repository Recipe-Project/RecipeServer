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
import com.recipe.app.src.common.application.BadWordService;
import com.recipe.app.src.recipe.application.dto.RecipeResponse;
import com.recipe.app.src.recipe.application.dto.RecipesResponse;
import com.recipe.app.src.recipe.domain.youtube.YoutubeRecipe;
import com.recipe.app.src.recipe.domain.youtube.YoutubeScrap;
import com.recipe.app.src.recipe.infra.youtube.YoutubeRecipeRepository;
import com.recipe.app.src.user.domain.User;
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
public class YoutubeRecipeService {

    private static final HttpTransport HTTP_TRANSPORT = new NetHttpTransport();
    private static final JsonFactory JSON_FACTORY = new JacksonFactory();
    private static final long NUMBER_OF_VIDEOS_RETURNED = 50;
    private static final String GOOGLE_YOUTUBE_URL = "https://www.youtube.com/watch?v=";
    private static final String YOUTUBE_SEARCH_FIELDS = "items(id/kind,id/videoId,snippet/title,snippet/channelTitle,snippet/thumbnails/default/url,snippet/publishedAt)";

    private final YoutubeRecipeRepository youtubeRecipeRepository;
    private final YoutubeScrapService youtubeScrapService;
    private final BadWordService badWordService;

    public YoutubeRecipeService(YoutubeRecipeRepository youtubeRecipeRepository, YoutubeScrapService youtubeScrapService, BadWordService badWordService) {
        this.youtubeRecipeRepository = youtubeRecipeRepository;
        this.youtubeScrapService = youtubeScrapService;
        this.badWordService = badWordService;
    }

    @Value("${google.api-key}")
    private String youtubeApiKey;

    @Transactional(readOnly = true)
    public RecipesResponse getYoutubeRecipes(User user, String keyword, int page, int size, String sort) throws IOException {

        badWordService.checkBadWords(keyword);

        Pageable pageable = PageRequest.of(page, size);
        Page<YoutubeRecipe> youtubeRecipes;
        if (sort.equals("youtubeScraps"))
            youtubeRecipes = youtubeRecipeRepository.findByTitleContainingOrDescriptionContainingOrderByYoutubeScrapSizeDesc(keyword, keyword, pageable);
        else if (sort.equals("youtubeViews"))
            youtubeRecipes = youtubeRecipeRepository.findByTitleContainingOrDescriptionContainingOrderByYoutubeViewSizeDesc(keyword, keyword, pageable);
        else
            youtubeRecipes = youtubeRecipeRepository.findByTitleContainingOrDescriptionContainingOrderByCreatedAtDesc(keyword, keyword, pageable);

        if (youtubeRecipes.getTotalElements() < 10) {

            searchYoutube(keyword);

            if (sort.equals("youtubeScraps"))
                youtubeRecipes = youtubeRecipeRepository.findByTitleContainingOrDescriptionContainingOrderByYoutubeScrapSizeDesc(keyword, keyword, pageable);
            else if (sort.equals("youtubeViews"))
                youtubeRecipes = youtubeRecipeRepository.findByTitleContainingOrDescriptionContainingOrderByYoutubeViewSizeDesc(keyword, keyword, pageable);
            else
                youtubeRecipes = youtubeRecipeRepository.findByTitleContainingOrDescriptionContainingOrderByCreatedAtDesc(keyword, keyword, pageable);

        }

        return new RecipesResponse(youtubeRecipes.getTotalElements(), youtubeRecipes.stream()
                .map((recipe) -> RecipeResponse.from(recipe, user))
                .collect(Collectors.toList()));
    }

    @Transactional(readOnly = true)
    public RecipesResponse getScrapYoutubeRecipes(User user, int page, int size) {

        Page<YoutubeScrap> youtubeScraps = youtubeScrapService.findByUserId(user.getUserId(), page, size);
        List<Long> youtubeRecipeIds = youtubeScraps.stream()
                .map(YoutubeScrap::getYoutubeRecipeId)
                .collect(Collectors.toList());
        List<YoutubeRecipe> youtubeRecipes = youtubeRecipeRepository.findAllById(youtubeRecipeIds);

        return new RecipesResponse(youtubeScraps.getTotalElements(), youtubeRecipes.stream()
                .map((youtubeRecipe) -> RecipeResponse.from(youtubeRecipe, user))
                .collect(Collectors.toList()));
    }

    @Transactional
    public void searchYoutube(String keyword) throws IOException {

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
            List<YoutubeRecipe> youtubeRecipes = new ArrayList<>();
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
            List<String> youtubeIds = youtubeRecipes.stream().map(YoutubeRecipe::getYoutubeId).collect(Collectors.toList());
            List<YoutubeRecipe> existYoutubeRecipes = youtubeRecipeRepository.findByYoutubeIdIn(youtubeIds);
            Map<String, YoutubeRecipe> existYoutubeRecipesMapByYoutubeId = existYoutubeRecipes.stream().collect(Collectors.toMap(YoutubeRecipe::getYoutubeId, v -> v));

            youtubeRecipeRepository.saveAll(youtubeRecipes.stream()
                    .filter(youtubeRecipe -> !existYoutubeRecipesMapByYoutubeId.containsKey(youtubeRecipe.getYoutubeId()))
                    .collect(Collectors.toList()));
        }
    }
}
