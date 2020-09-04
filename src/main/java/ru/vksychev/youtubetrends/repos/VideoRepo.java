package ru.vksychev.youtubetrends.repos;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.vksychev.youtubetrends.domain.Video;

public interface VideoRepo extends JpaRepository<Video, String> {
}
