package com.translation.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TranslationDto {
    
    private Long id;
    
    @NotBlank(message = "Key is required")
    @Size(max = 255, message = "Key must not exceed 255 characters")
    private String key;
    
    @NotBlank(message = "Locale is required")
    @Size(max = 10, message = "Locale must not exceed 10 characters")
    private String locale;
    
    @NotBlank(message = "Content is required")
    private String content;
    
    private Set<String> tags;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    public TranslationDto(String key, String locale, String content, Set<String> tags) {
        this.key = key;
        this.locale = locale;
        this.content = content;
        this.tags = tags;
    }
}