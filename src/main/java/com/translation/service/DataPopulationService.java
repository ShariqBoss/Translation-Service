package com.translation.service;

import com.translation.entity.Tag;
import com.translation.entity.Translation;
import com.translation.repository.TagRepository;
import com.translation.repository.TranslationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class DataPopulationService {
    @Autowired
    private TranslationRepository translationRepository;
    @Autowired
    private TagRepository tagRepository;
    
    private static final String[] LOCALES = {"en", "fr", "es", "de"};
    private static final String[] TAG_NAMES = {"mobile", "desktop", "web", "info"};
    private static final String[] KEY_PREFIXES = {"button", "label", "message", "title", "description"};
    
    @Transactional
    public void populateDatabase(int recordCount) {
        System.out.println("Starting database population with " + recordCount + " records...");
        
        List<Tag> tags = createTags();
        int batchSize = 1000;
        for (int i = 0; i < recordCount; i += batchSize) {
            int endIndex = Math.min(i + batchSize, recordCount);
            List<Translation> batch = createTranslationBatch(i, endIndex, tags);
            translationRepository.saveAll(batch);
            
            if ((i + batchSize) % 10000 == 0) {
                System.out.println("Created " + (i + batchSize) + " translations...");
            }
        }
        System.out.println("Database population completed with " + recordCount + " records.");
    }
    
    private List<Tag> createTags() {
        List<Tag> tags = new ArrayList<>();
        for (String tagName : TAG_NAMES) {
            if (!tagRepository.findByName(tagName).isPresent()) {
                tags.add(new Tag(tagName));
            }
        }
        return tagRepository.saveAll(tags);
    }
    
    private List<Translation> createTranslationBatch(int startIndex, int endIndex, List<Tag> availableTags) {
        List<Translation> translations = new ArrayList<>();
        Random random = ThreadLocalRandom.current();
        
        for (int i = startIndex; i < endIndex; i++) {
            String keyPrefix = KEY_PREFIXES[random.nextInt(KEY_PREFIXES.length)];
            String key = keyPrefix + "." + (i % 1000);
            String locale = LOCALES[random.nextInt(LOCALES.length)];
            String content = generateContent(key, locale, random);
            
            Translation translation = new Translation(key, locale, content);
            int tagCount = random.nextInt(4);
            Set<Tag> translationTags = new HashSet<>();
            for (int j = 0; j < tagCount; j++) {
                Tag randomTag = availableTags.get(random.nextInt(availableTags.size()));
                translationTags.add(randomTag);
            }
            translation.setTags(translationTags);
            
            translations.add(translation);
        }
        
        return translations;
    }
    
    private String generateContent(String key, String locale, Random random) {
        String baseContent = "Sample content for " + key;   
        switch (locale) {
            case "fr":
                return "Contenu d'exemple pour " + key;
            case "es":
                return "Contenido de ejemplo para " + key;
            case "de":
                return "Beispielinhalt für " + key;
            case "en":
                return "Sample content for English " + key;
            default:
                return baseContent;
        }
    }
}