package edu.eci.arep.microtwitter.backendtwitter.config;

import edu.eci.arep.microtwitter.backendtwitter.model.Stream;
import edu.eci.arep.microtwitter.backendtwitter.model.Usuario;
import edu.eci.arep.microtwitter.backendtwitter.repository.StreamRepository;
import edu.eci.arep.microtwitter.backendtwitter.repository.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner initDatabase(StreamRepository streamRepository,
                                   UsuarioRepository usuarioRepository) {
        return args -> {
            Stream global = new Stream();
            global.setName("global");
            streamRepository.save(global);

            Usuario test = new Usuario();
            test.setUsername("testuser");
            test.setEmail("test@example.com");
            test.setCognitoSub("temp-user");
            usuarioRepository.save(test);
        };
    }
}
