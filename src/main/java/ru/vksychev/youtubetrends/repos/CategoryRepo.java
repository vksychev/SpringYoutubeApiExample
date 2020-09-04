package ru.vksychev.youtubetrends.repos;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.vksychev.youtubetrends.domain.Category;

public interface CategoryRepo extends JpaRepository<Category, Integer> {
}
