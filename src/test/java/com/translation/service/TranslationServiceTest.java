package com.translation.service;

import com.translation.dto.TranslationDto;
import com.translation.entity.Tag;
import com.translation.entity.Translation;
import com.translation.repository.TagRepository;
import com.translation.repository.TranslationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.eq;

@ExtendWith(MockitoExtension.class)
class TranslationServiceTest {
    
    @Mock
    private TranslationRepository translationRepository;
    
    @Mock
    private TagRepository tagRepository;
    
    @InjectMocks
    private TranslationService translationService;
    
    private TranslationDto translationDto;
    private Translation translation;
    private Tag tag;
    
    @BeforeEach
    void setUp() {
        tag = new Tag("mobile");
        tag.setId(1L);
        translation = new Translation("button.save", "en", "Save");
        translation.setId(1L);
        translation.getTags().add(tag);
        translationDto = new TranslationDto("button.save", "en", "Save", Set.of("mobile"));
        translationDto.setId(1L);
    }
    
    @Test
    void createTranslation_Success() {
        when(translationRepository.findByKeyAndLocale("button.save", "en")).thenReturn(Optional.empty());
        when(tagRepository.findByNameIn(anyList())).thenReturn(Set.of(tag));
        when(translationRepository.save(any(Translation.class))).thenReturn(translation);
        TranslationDto result = translationService.createTranslation(translationDto);
        assertNotNull(result);
        assertEquals("button.save", result.getKey());
        assertEquals("en", result.getLocale());
        assertEquals("Save", result.getContent());
        assertTrue(result.getTags().contains("mobile"));
        verify(translationRepository).save(any(Translation.class));
    }
    
    @Test
    void createTranslation_AlreadyExists() {
        when(translationRepository.findByKeyAndLocale("button.save", "en")).thenReturn(Optional.of(translation));
        assertThrows(IllegalArgumentException.class, () -> {
            translationService.createTranslation(translationDto);
        });
        verify(translationRepository, never()).save(any(Translation.class));
    }
    
    @Test
    void updateTranslation_Success() {
        when(translationRepository.findById(1L)).thenReturn(Optional.of(translation));
        when(tagRepository.findByNameIn(anyList())).thenReturn(Set.of(tag));
        when(translationRepository.save(any(Translation.class))).thenReturn(translation);
        TranslationDto result = translationService.updateTranslation(1L, translationDto);
        assertNotNull(result);
        assertEquals("button.save", result.getKey());
        verify(translationRepository).save(any(Translation.class));
    }
    
    @Test
    void updateTranslation_NotFound() {
        when(translationRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class, () -> {
            translationService.updateTranslation(1L, translationDto);
        });
    }
    
    @Test
    void getTranslation_Success() {
        when(translationRepository.findById(1L)).thenReturn(Optional.of(translation));
        Optional<TranslationDto> result = translationService.getTranslation(1L);
        assertTrue(result.isPresent());
        assertEquals("button.save", result.get().getKey());
    }
    
    @Test
    void searchTranslations_Success() {
        List<Translation> translations = Arrays.asList(translation);
        Page<Translation> page = new PageImpl<>(translations);
        when(translationRepository.searchTranslations(eq("button"), eq(null), eq("en"), any(Pageable.class)))
            .thenReturn(page);
        Page<TranslationDto> result = translationService.searchTranslations("button", null, "en", null, 0, 10);
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals("button.save", result.getContent().get(0).getKey());
    }
    
    @Test
    void getTranslationsForLocale_Success() {
        List<Translation> translations = Arrays.asList(translation);
        when(translationRepository.findByLocale("en")).thenReturn(translations);
        Map<String, String> result = translationService.getTranslationsForLocale("en");
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Save", result.get("button.save"));
    }
    
    @Test
    void deleteTranslation_Success() {
        when(translationRepository.findById(1L)).thenReturn(Optional.of(translation));
        translationService.deleteTranslation(1L);
        verify(translationRepository).delete(translation);
    }
    
    @Test
    void deleteTranslation_NotFound() {
        when(translationRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class, () -> {
            translationService.deleteTranslation(1L);
        });
    }
}