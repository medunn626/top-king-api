package application.topkingapi.repo;

import application.topkingapi.model.Video;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VideoRepo extends JpaRepository<Video, Long> {
}
