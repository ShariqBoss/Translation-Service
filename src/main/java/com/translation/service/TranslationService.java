package com.translation.service;

import com.translation.dto.TranslationDto;
import com.translation.entity.Tag;
import com.translation.entity.Translation;
import com.translation.repository.TagRepository;
import com.translation.repository.TranslationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class TranslationService {
    
    @Autowired
    private TranslationRepository translationRepository;
    @Autowired
    private TagRepository tagRepository;
    
    public TranslationDto createTranslation(TranslationDto dto) {
        Optional<Translation> existing = translationRepository.findByKeyAndLocale(dto.getKey(), dto.getLocale());
        if (existing.isPresent()) {
            throw new IllegalArgumentException("Translation already exists for key: " + dto.getKey() + 
            " and locale: " + dto.getLocale());
        }
        
        Translation translation = new Translation(dto.getKey(), dto.getLocale(), dto.getContent());
        translation.setTags(getOrCreateTags(dto.getTags()));
        
        Translation saved = translationRepository.save(translation);
        return convertToDto(saved);
    }
    
    public TranslationDto updateTranslation(Long id, TranslationDto dto) {
        Translation translation = translationRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Translation not found with id: " + id));
        
        translation.setKey(dto.getKey());
        translation.setLocale(dto.getLocale());
        translation.setContent(dto.getContent());
        translation.setTags(getOrCreateTags(dto.getTags()));
        
        Translation saved = translationRepository.save(translation);
        return convertToDto(saved);
    }
    
    @Transactional(readOnly = true)
    public Optional<TranslationDto> getTranslation(Long id) {
        return translationRepository.findById(id).map(this::convertToDto);
    }
    
    @Transactional(readOnly = true)
    public Page<TranslationDto> searchTranslations(String key, String content, String locale, 
                                                  List<String> tags, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("updatedAt").descending());
        
        Page<Translation> translations;
        if (tags != null && !tags.isEmpty()) {
            translations = translationRepository.findByTagsIn(tags, pageable);
        } else {
            translations = translationRepository.searchTranslations(key, content, locale, pageable);
        }
        
        return translations.map(this::convertToDto);
    }
    
    @Cacheable(value = "translations", key = "#locale")
    @Transactional(readOnly = true)
    public Map<String, String> getTranslationsForLocale(String locale) {
        List<Translation> translations = translationRepository.findByLocale(locale);
        return translations.stream()
            .collect(Collectors.toMap(Translation::getKey, Translation::getContent));
    }
    
    public void deleteTranslation(Long id) {
        Translation translation = translationRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Translation not found with id: " + id));
        
        translationRepository.delete(translation);
    }
    
    @Transactional(readOnly = true)
    public List<String> getAvailableLocales() {
        return translationRepository.findDistinctLocales();
    }
    
    private Set<Tag> getOrCreateTags(Set<String> tagNames) {
        if (tagNames == null || tagNames.isEmpty()) {
            return new HashSet<>();
        }
        
        Set<Tag> existingTags = tagRepository.findByNameIn(new ArrayList<>(tagNames));
        Set<String> existingTagNames = existingTags.stream()
            .map(Tag::getName)
            .collect(Collectors.toSet());
        
        Set<Tag> newTags = tagNames.stream()
            .filter(name -> !existingTagNames.contains(name))
            .map(Tag::new)
            .collect(Collectors.toSet());
        
        if (!newTags.isEmpty()) {
            tagRepository.saveAll(newTags);
            existingTags.addAll(newTags);
        }
        
        return existingTags;
    }
    
    private TranslationDto convertToDto(Translation translation) {
        Set<String> tagNames = translation.getTags().stream()
            .map(Tag::getName)
            .collect(Collectors.toSet());
        
        TranslationDto dto = new TranslationDto(
            translation.getKey(), 
            translation.getLocale(), 
            translation.getContent(), 
            tagNames
        );
        dto.setId(translation.getId());
        dto.setCreatedAt(translation.getCreatedAt());
        dto.setUpdatedAt(translation.getUpdatedAt());
        
        return dto;
    }
    
}