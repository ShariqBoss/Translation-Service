package com.translation.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "translations", indexes = {
    @Index(name = "idx_key_locale", columnList = "translation_key, locale"),
    @Index(name = "idx_locale", columnList = "locale"),
    @Index(name = "idx_key", columnList = "translation_key"),
    @Index(name = "idx_updated_at", columnList = "updated_at")
})
@Data
@NoArgsConstructor
@ToString(exclude = "tags")
public class Translation {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank
    @Size(max = 255)
    @Column(name = "translation_key", nullable = false)
    private String key;
    
    @NotBlank
    @Size(max = 10)
    @Column(nullable = false, length = 10)
    private String locale;
    
    @NotBlank
    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;
    
    @ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
        name = "translation_tags",
        joinColumns = @JoinColumn(name = "translation_id"),
        inverseJoinColumns = @JoinColumn(name = "tag_id"),
        indexes = {
            @Index(name = "idx_translation_tags_translation", columnList = "translation_id"),
            @Index(name = "idx_translation_tags_tag", columnList = "tag_id")
        }
    )
    private Set<Tag> tags = new HashSet<>();
    
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    public Translation(String key, String locale, String content) {
        this.key = key;
        this.locale = locale;
        this.content = content;
    }
}