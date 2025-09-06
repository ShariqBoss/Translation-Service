package com.translation.repository;

import com.translation.entity.Translation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TranslationRepository extends JpaRepository<Translation, Long> {
    
    Optional<Translation> findByKeyAndLocale(String key, String locale);
    
    @Query("SELECT t FROM Translation t WHERE t.locale = :locale")
    List<Translation> findByLocale(@Param("locale") String locale);
    
    @Query("SELECT t FROM Translation t JOIN FETCH t.tags WHERE t.locale = :locale")
    List<Translation> findByLocaleWithTags(@Param("locale") String locale);
    
    @Query("SELECT t FROM Translation t JOIN t.tags tag WHERE tag.name IN :tags")
    Page<Translation> findByTagsIn(@Param("tags") List<String> tags, Pageable pageable);
    
    @Query("SELECT t FROM Translation t WHERE " +
           "(:key IS NULL OR LOWER(t.key) LIKE LOWER(CONCAT('%', :key, '%'))) AND " +
           "(:content IS NULL OR LOWER(t.content) LIKE LOWER(CONCAT('%', :content, '%'))) AND " +
           "(:locale IS NULL OR t.locale = :locale)")
    Page<Translation> searchTranslations(@Param("key") String key, 
                                       @Param("content") String content, 
                                       @Param("locale") String locale, 
                                       Pageable pageable);
    
    @Query("SELECT DISTINCT t.locale FROM Translation t ORDER BY t.locale")
    List<String> findDistinctLocales();
    
    @Query("SELECT COUNT(t) FROM Translation t WHERE t.locale = :locale")
    long countByLocale(@Param("locale") String locale);
    
    @Query("SELECT MAX(t.updatedAt) FROM Translation t WHERE t.locale = :locale")
    java.time.LocalDateTime findLastUpdatedByLocale(@Param("locale") String locale);
}