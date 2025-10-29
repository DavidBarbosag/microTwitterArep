package edu.eci.arep.microtwitter.backendtwitter.repository;

import edu.eci.arep.microtwitter.backendtwitter.model.Stream;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface StreamRepository extends JpaRepository<Stream, Long> {
    Optional<Stream> findByName(String name);
}

