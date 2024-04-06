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
import com.recipe.app.src.recipe.domain.youtube.YoutubeView;
import com.recipe.app.src.recipe.exception.NotFoundRecipeException;
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
import java.util.function.Function;
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
    private final YoutubeViewService youtubeViewService;
    private final BadWordService badWordService;

    public YoutubeRecipeService(YoutubeRecipeRepository youtubeRecipeRepository, YoutubeScrapService youtubeScrapService, YoutubeViewService youtubeViewService, BadWordService badWordService) {
        this.youtubeRecipeRepository = youtubeRecipeRepository;
        this.youtubeScrapService = youtubeScrapService;
        this.youtubeViewService = youtubeViewService;
        this.badWordService = badWordService;
    }

    @Value("${google.api-key}")
    private String youtubeApiKey;

    @Transactional(readOnly = true)
    public RecipesResponse getYoutubeRecipes(User user, String keyword, Long lastYoutubeRecipeId, int size, String sort) throws IOException {

        badWordService.checkBadWords(keyword);

        long totalCnt = youtubeRecipeRepository.countByKeyword(keyword);

        if (totalCnt < 10) {
            searchYoutube(keyword);
            totalCnt = youtubeRecipeRepository.countByKeyword(keyword);
        }
        List<YoutubeRecipe> youtubeRecipes = findByKeywordSortBy(keyword, lastYoutubeRecipeId, size, sort);

        return getRecipes(user, totalCnt, youtubeRecipes);
    }

    private List<YoutubeRecipe> findByKeywordSortBy(String keyword, Long lastYoutubeRecipeId, int size, String sort) {

        if (sort.equals("youtubeScraps")) {
            long youtubeScrapCnt = youtubeScrapService.countByYoutubeRecipeId(lastYoutubeRecipeId);
            return youtubeRecipeRepository.findByKeywordLimitOrderByYoutubeScrapCntDesc(keyword, lastYoutubeRecipeId, youtubeScrapCnt, size);
        } else if (sort.equals("youtubeViews")) {
            long youtubeViewCnt = youtubeViewService.countByYoutubeRecipeId(lastYoutubeRecipeId);
            return youtubeRecipeRepository.findByKeywordLimitOrderByYoutubeViewCntDesc(keyword, lastYoutubeRecipeId, youtubeViewCnt, size);
        } else {
            YoutubeRecipe youtubeRecipe = null;
            if (lastYoutubeRecipeId != null && lastYoutubeRecipeId > 0) {
                youtubeRecipe = youtubeRecipeRepository.findById(lastYoutubeRecipeId).orElseThrow(() -> {
                    throw new NotFoundRecipeException();
                });
            }
            return youtubeRecipeRepository.findByKeywordLimitOrderByPostDateDesc(keyword, lastYoutubeRecipeId, youtubeRecipe != null ? youtubeRecipe.getPostDate() : null, size);
        }
    }

    @Transactional(readOnly = true)
    public RecipesResponse getScrapYoutubeRecipes(User user, Long lastYoutubeRecipeId, int size) {

        long totalCnt = youtubeScrapService.countYoutubeScrapByUser(user);
        YoutubeScrap youtubeScrap = null;
        if (lastYoutubeRecipeId != null && lastYoutubeRecipeId > 0) {
            youtubeScrap = youtubeScrapService.findByUserIdAndYoutubeRecipeId(user.getUserId(), lastYoutubeRecipeId);
        }
        List<YoutubeRecipe> youtubeRecipes = youtubeRecipeRepository.findUserScrapYoutubeRecipesLimit(user.getUserId(), lastYoutubeRecipeId, youtubeScrap != null ? youtubeScrap.getCreatedAt() : null, size);

        return getRecipes(user, totalCnt, youtubeRecipes);
    }

    private RecipesResponse getRecipes(User user, long totalCnt, List<YoutubeRecipe> youtubeRecipes) {

        List<Long> youtubeRecipeIds = youtubeRecipes.stream()
                .map(YoutubeRecipe::getYoutubeRecipeId)
                .collect(Collectors.toList());
        List<YoutubeScrap> youtubeScraps = youtubeScrapService.findByYoutubeRecipeIds(youtubeRecipeIds);
        List<YoutubeView> youtubeViews = youtubeViewService.findByYoutubeRecipeIds(youtubeRecipeIds);

        return new RecipesResponse(totalCnt, youtubeRecipes.stream()
                .map((youtubeRecipe) -> {
                    long scrapCnt = getYoutubeScrapCnt(youtubeScraps, youtubeRecipe.getYoutubeRecipeId(), user);
                    long viewCnt = getYoutubeViewCnt(youtubeViews, youtubeRecipe.getYoutubeRecipeId(), user);
                    boolean isUserScrap = isUserScrap(youtubeScraps, youtubeRecipe.getYoutubeRecipeId(), user);

                    return RecipeResponse.from(youtubeRecipe, isUserScrap, scrapCnt, viewCnt);
                })
                .collect(Collectors.toList()));
    }

    private boolean isUserScrap(List<YoutubeScrap> youtubeScraps, Long youtubeRecipeId, User user) {
        return youtubeScraps.stream()
                .anyMatch(youtubeScrap -> youtubeScrap.getYoutubeRecipeId().equals(youtubeRecipeId) && youtubeScrap.getUserId().equals(user.getUserId()));
    }

    private long getYoutubeViewCnt(List<YoutubeView> youtubeViews, Long youtubeRecipeId, User user) {
        return youtubeViews.stream()
                .filter(youtubeView -> youtubeView.getYoutubeRecipeId().equals(youtubeRecipeId) && youtubeView.getUserId().equals(user.getUserId()))
                .count();
    }

    private long getYoutubeScrapCnt(List<YoutubeScrap> youtubeScraps, Long youtubeRecipeId, User user) {
        return youtubeScraps.stream()
                .filter(youtubeScrap -> youtubeScrap.getYoutubeRecipeId().equals(youtubeRecipeId) && youtubeScrap.getUserId().equals(user.getUserId()))
                .count();
    }

    private void searchYoutube(String keyword) throws IOException {

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

    @Transactional
    public void createYoutubeScrap(User user, Long youtubeRecipeId) {

        YoutubeRecipe youtubeRecipe = youtubeRecipeRepository.findById(youtubeRecipeId).orElseThrow(() -> {
            throw new NotFoundRecipeException();
        });
        youtubeRecipe.plusScrapCnt();
        youtubeRecipeRepository.save(youtubeRecipe);
        youtubeScrapService.createYoutubeScrap(user, youtubeRecipeId);
    }

    @Transactional
    public void deleteYoutubeScrap(User user, Long youtubeRecipeId) {

        YoutubeRecipe youtubeRecipe = youtubeRecipeRepository.findById(youtubeRecipeId).orElseThrow(() -> {
            throw new NotFoundRecipeException();
        });
        youtubeRecipe.minusScrapCnt();
        youtubeRecipeRepository.save(youtubeRecipe);
        youtubeScrapService.deleteYoutubeScrap(user, youtubeRecipeId);
    }

    @Transactional
    public void createYoutubeView(User user, Long youtubeRecipeId) {

        YoutubeRecipe youtubeRecipe = youtubeRecipeRepository.findById(youtubeRecipeId).orElseThrow(() -> {
            throw new NotFoundRecipeException();
        });
        youtubeRecipe.plusViewCnt();
        youtubeRecipeRepository.save(youtubeRecipe);
        youtubeViewService.createYoutubeView(user, youtubeRecipeId);
    }
}
