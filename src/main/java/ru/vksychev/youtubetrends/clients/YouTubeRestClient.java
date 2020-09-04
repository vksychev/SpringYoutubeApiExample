package ru.vksychev.youtubetrends.clients;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class YouTubeRestClient {

    //Google API information
    @Value("${google.url.get.videos}")
    private String videoUrl;
    @Value("${google.url.get.channel}")
    private String channelUrl;
    @Value("${google.url.get.categories}")
    private String categoryUrl;

    @Value("${google.api.key}")
    private String secretKey;

    private RestTemplate restTemplate;
    private HttpHeaders headers;

    public YouTubeRestClient() {
        this.restTemplate = new RestTemplate();
        headers = new HttpHeaders();
        restTemplate.getMessageConverters()
                .add(0, new StringHttpMessageConverter(StandardCharsets.UTF_8));

    }

    public JsonNode getVideoStatistics(String videoId) throws JsonProcessingException {
        return getSingleItem("statistics", videoId, videoUrl, "statistics");
    }

    public JsonNode getChannelStatistics(String channelId) throws JsonProcessingException {
        return getSingleItem("statistics", channelId, channelUrl, "statistics");
    }

    public JsonNode getVideoSnippet(String channelId) throws JsonProcessingException {
        return getSingleItem("snippet", channelId, videoUrl, "snippet");
    }

    public JsonNode getCategorySnippet(String categoryId) throws JsonProcessingException {
        return getSingleItem("snippet", categoryId, categoryUrl, "snippet");
    }

    public List<String> getMostPopular(String regionCode, int maxResults) throws JsonProcessingException {
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(videoUrl)
                .queryParam("part", "contentDetails")
                .queryParam("chart", "mostPopular")
                .queryParam("regionCode", regionCode)
                .queryParam("maxResults", maxResults)
                .queryParam("key", secretKey);
        JsonNode responseJson = getRequest(builder);
        ArrayNode itemsNode = (ArrayNode) responseJson.get("items");
        List<String> idList = new ArrayList<>();
        for (JsonNode node : itemsNode) {
            idList.add(node.get("id").asText());
        }
        return idList;
    }

    private JsonNode getSingleItem(String part, String id, String url, String jsonPath)
            throws JsonProcessingException {
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url)
                .queryParam("part", part)
                .queryParam("id", id)
                .queryParam("key", secretKey);
        JsonNode responseJson = getRequest(builder);
        ArrayNode itemsNode = (ArrayNode) responseJson.get("items");
        JsonNode firstNode = itemsNode.get(0);
        return firstNode.path(jsonPath);
    }

    private JsonNode getRequest(UriComponentsBuilder builder) throws JsonProcessingException {
        HttpEntity<String> requestEntity = new HttpEntity<>(headers);
        ResponseEntity<String> responseEntity = restTemplate.exchange(
                builder.toUriString(),
                HttpMethod.GET,
                requestEntity,
                String.class);
        return new ObjectMapper().readTree(responseEntity.getBody());
    }
}
