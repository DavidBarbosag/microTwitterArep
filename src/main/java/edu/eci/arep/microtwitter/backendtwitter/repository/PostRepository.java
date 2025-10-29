package edu.eci.arep.microtwitter.backendtwitter.repository;

import edu.eci.arep.microtwitter.backendtwitter.model.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {
    List<Post> findByStreamIdOrderByCreatedAtDesc(Long streamId);
}
