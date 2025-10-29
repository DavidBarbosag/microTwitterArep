package edu.eci.arep.microtwitter.backendtwitter.service;

import edu.eci.arep.microtwitter.backendtwitter.dto.PostDTO;
import edu.eci.arep.microtwitter.backendtwitter.dto.PostResponseDTO;
import edu.eci.arep.microtwitter.backendtwitter.model.Post;
import edu.eci.arep.microtwitter.backendtwitter.model.Stream;
import edu.eci.arep.microtwitter.backendtwitter.model.Usuario;
import edu.eci.arep.microtwitter.backendtwitter.repository.PostRepository;
import edu.eci.arep.microtwitter.backendtwitter.repository.StreamRepository;
import edu.eci.arep.microtwitter.backendtwitter.repository.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PostService {
    private final PostRepository postRepository;
    private final UsuarioRepository usuarioRepository;
    private final StreamRepository streamRepository;

    public PostService(PostRepository postRepository,
                       UsuarioRepository usuarioRepository,
                       StreamRepository streamRepository) {
        this.postRepository = postRepository;
        this.usuarioRepository = usuarioRepository;
        this.streamRepository = streamRepository;
    }

    @Transactional
    public PostResponseDTO createPost(String cognitoSub, PostDTO postDTO) {
        if (postDTO.getText().length() > 140) {
            throw new IllegalArgumentException("El post no puede tener más de 140 caracteres");
        }

        Usuario usuario = usuarioRepository.findByCognitoSub(cognitoSub)
                .orElseGet(() -> {
                    // Auto-crear usuario si no existe
                    Usuario newUser = new Usuario();
                    newUser.setCognitoSub(cognitoSub);
                    newUser.setUsername("user_" + cognitoSub.substring(0, 8));
                    newUser.setEmail(cognitoSub + "@cognito.user");
                    return usuarioRepository.save(newUser);
                });

        Stream globalStream = streamRepository.findByName("global")
                .orElseGet(() -> {
                    Stream s = new Stream();
                    s.setName("global");
                    return streamRepository.save(s);
                });

        Post post = new Post();
        post.setText(postDTO.getText());
        post.setAuthor(usuario);
        post.setStream(globalStream);

        Post saved = postRepository.save(post);

        return new PostResponseDTO(
                saved.getId(),
                saved.getText(),
                saved.getAuthor().getUsername(),
                saved.getCreatedAt()
        );
    }


    public List<PostResponseDTO> getGlobalPosts() {
        Stream globalStream = streamRepository.findByName("global")
                .orElseThrow(() -> new RuntimeException("Stream global no encontrado"));

        return postRepository.findByStreamIdOrderByCreatedAtDesc(globalStream.getId())
                .stream()
                .map(p -> new PostResponseDTO(
                        p.getId(),
                        p.getText(),
                        p.getAuthor().getUsername(),
                        p.getCreatedAt()
                ))
                .collect(Collectors.toList());
    }
}
