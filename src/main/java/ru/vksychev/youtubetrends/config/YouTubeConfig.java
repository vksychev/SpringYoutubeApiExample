package ru.vksychev.youtubetrends.config;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import ru.vksychev.youtubetrends.clients.YouTubeRestClient;
import ru.vksychev.youtubetrends.domain.Category;
import ru.vksychev.youtubetrends.domain.Region;
import ru.vksychev.youtubetrends.domain.User;
import ru.vksychev.youtubetrends.domain.Video;
import ru.vksychev.youtubetrends.repos.CategoryRepo;
import ru.vksychev.youtubetrends.repos.RegionRepo;
import ru.vksychev.youtubetrends.repos.UserRepo;
import ru.vksychev.youtubetrends.repos.VideoRepo;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.List;

@Configuration
@EnableScheduling
@Slf4j
public class YouTubeConfig {

    final YouTubeRestClient restClient;
    private final VideoRepo videoRepo;
    private final UserRepo userRepo;
    private final CategoryRepo categoryRepo;
    private final RegionRepo regionRepo;


    public YouTubeConfig(YouTubeRestClient restClient, VideoRepo videoRepo, UserRepo userRepo, CategoryRepo categoryRepo, RegionRepo regionRepo) {
        this.restClient = restClient;
        this.videoRepo = videoRepo;
        this.userRepo = userRepo;
        this.categoryRepo = categoryRepo;
        this.regionRepo = regionRepo;
    }

    @Scheduled(fixedDelay = 10000)
    public void start() throws IOException {
        List<Region> regions = regionRepo.findAll();
        for (Region region : regions) {
            processVideosForRegion(region);
        }
    }

    public void processVideosForRegion(Region region) throws IOException {
        log.debug("INFO: region set {}", region.toString());
        List<String> videoList = restClient.getMostPopular(region.getAbb(), 50);
        for (String element : videoList) {
            processVideo(element, videoList.indexOf(element), region);
        }
        log.debug("INFO: {}", "iteration end");
    }

    public void processVideo(String id, int position, Region region) throws JsonProcessingException {
        if (!videoRepo.existsById(id)) {
            Video curVideo = new Video();

            curVideo.setId(id);
            curVideo.setRegions(new HashSet<>());
            curVideo.getRegions().add(region);
            curVideo.setTrendNum(position + 1);
            JsonNode videoStats = restClient.getVideoStatistics(id);

            curVideo.setComments(videoStats.get("commentCount").asInt());
            curVideo.setDislikes(videoStats.get("dislikeCount").asInt());
            curVideo.setLikes(videoStats.get("likeCount").asInt());
            curVideo.setViews(videoStats.get("viewCount").asInt());
            curVideo.setTrendMoment(LocalDateTime.now());
            JsonNode videoSnippet = restClient.getVideoSnippet(id);

            curVideo.setName(videoSnippet.get("title").asText());

            String publishedAt = videoSnippet.get("publishedAt").asText();

            DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'");
            DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            LocalDateTime date = LocalDateTime.parse(publishedAt, inputFormatter);
            String formattedDate = outputFormatter.format(date);
            LocalDateTime dateTime = LocalDateTime.parse(formattedDate, outputFormatter);
            curVideo.setCreationDate(dateTime);

            User curUser = getUser(videoSnippet);
            curVideo.setUser(curUser);

            Category curCategory = getCategory(videoSnippet);
            curVideo.setCategory(curCategory);

            userRepo.save(curUser);
            categoryRepo.save(curCategory);

            videoRepo.save(curVideo);
        } else {
            Video curVideo = videoRepo.findById(id).orElseThrow(NullPointerException::new);
            curVideo.setTrendNum(position + 1);
            curVideo.setTrendMoment(LocalDateTime.now());
            curVideo.getRegions().add(region);
            curVideo.getUser()
                    .setSubs(
                            restClient.getChannelStatistics(curVideo.getUser().getId()).get("subscriberCount").asInt()
                    );

            videoRepo.save(curVideo);
        }
    }

    private User getUser(JsonNode videoSnippet) throws JsonProcessingException {
        User curUser = new User();
        String channelId = videoSnippet.get("channelId").asText();
        curUser.setId(channelId);
        curUser.setName(videoSnippet.get("channelTitle").asText());
        curUser.setSubs(restClient.getChannelStatistics(channelId).get("subscriberCount").asInt());
        return curUser;
    }

    private Category getCategory(JsonNode videoSnippet) throws JsonProcessingException {
        Category curCategory = new Category();
        Integer categoryId = videoSnippet.get("categoryId").asInt();
        if (!categoryRepo.existsById(categoryId)) {
            curCategory.setId(categoryId);
            curCategory.setName(restClient.getCategorySnippet(categoryId.toString()).get("title").asText());
        } else {
            curCategory = categoryRepo.getOne(categoryId);
        }
        return curCategory;
    }
}
