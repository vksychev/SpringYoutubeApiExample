package ru.vksychev.youtubetrends.repos;

import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Repository;
import ru.vksychev.youtubetrends.clients.YouTubeRestClient;

@Repository
public class YouTubeApiRepo {

    @Bean
    YouTubeRestClient youTubeRestClient(){
        return new YouTubeRestClient();
    }
}
