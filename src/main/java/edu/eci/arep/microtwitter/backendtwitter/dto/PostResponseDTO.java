package edu.eci.arep.microtwitter.backendtwitter.dto;

import java.time.LocalDateTime;

public class PostResponseDTO {
    private Long id;
    private String text;
    private AuthorDTO author;
    private LocalDateTime createdAt;

    public PostResponseDTO(Long id, String text, String username, LocalDateTime createdAt) {
        this.id = id;
        this.text = text;
        this.author = new AuthorDTO(username);
        this.createdAt = createdAt;
    }

    public Long getId() { return id; }
    public String getText() { return text; }
    public AuthorDTO getAuthor() { return author; }
    public LocalDateTime getCreatedAt() { return createdAt; }

    public static class AuthorDTO {
        private String username;

        public AuthorDTO(String username) { this.username = username; }
        public String getUsername() { return username; }
    }
}
