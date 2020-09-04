package ru.vksychev.youtubetrends.repos;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.vksychev.youtubetrends.domain.User;

public interface UserRepo extends JpaRepository<User, String> {
}
