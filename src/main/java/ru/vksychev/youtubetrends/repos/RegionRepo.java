package ru.vksychev.youtubetrends.repos;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.vksychev.youtubetrends.domain.Region;

public interface RegionRepo extends JpaRepository<Region, Integer> {
}
