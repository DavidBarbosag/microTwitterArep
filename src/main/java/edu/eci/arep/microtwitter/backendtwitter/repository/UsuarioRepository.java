package edu.eci.arep.microtwitter.backendtwitter.repository;

import edu.eci.arep.microtwitter.backendtwitter.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByUsername(String username);
    Optional<Usuario> findByCognitoSub(String cognitoSub);
}
