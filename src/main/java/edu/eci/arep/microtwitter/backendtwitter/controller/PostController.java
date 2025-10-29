package edu.eci.arep.microtwitter.backendtwitter.controller;

import edu.eci.arep.microtwitter.backendtwitter.dto.PostDTO;
import edu.eci.arep.microtwitter.backendtwitter.dto.PostResponseDTO;
import edu.eci.arep.microtwitter.backendtwitter.service.PostService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/streams/global/posts")
public class PostController {
    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    @GetMapping
    public ResponseEntity<List<PostResponseDTO>> getPosts() {
        return ResponseEntity.ok(postService.getGlobalPosts());
    }

    @PostMapping
    public ResponseEntity<PostResponseDTO> createPost(
            @AuthenticationPrincipal Jwt jwt,
            @RequestBody PostDTO postDTO) {

        String cognitoSub = jwt.getSubject(); // Obtener el "sub" del token JWT
        PostResponseDTO created = postService.createPost(cognitoSub, postDTO);
        return ResponseEntity.ok(created);
    }
}
