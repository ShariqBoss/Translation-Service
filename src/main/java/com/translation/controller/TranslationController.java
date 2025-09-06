package com.translation.controller;

import com.translation.dto.TranslationDto;
import com.translation.service.TranslationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/translations")
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Translations", description = "Translation management endpoints")
public class TranslationController {
    
    @Autowired
    private TranslationService translationService;
    
    @PostMapping("/create")
    @Operation(summary = "Create a new translation")
    public ResponseEntity<TranslationDto> createTranslation(@Valid @RequestBody TranslationDto dto) {
        TranslationDto created = translationService.createTranslation(dto);
        return ResponseEntity.ok(created);
    }
    
    @PutMapping("/update/{id}")
    @Operation(summary = "Update an existing translation")
    public ResponseEntity<TranslationDto> updateTranslation(
            @PathVariable Long id, 
            @Valid @RequestBody TranslationDto dto) {
        TranslationDto updated = translationService.updateTranslation(id, dto);
        return ResponseEntity.ok(updated);
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Get translation by ID")
    public ResponseEntity<TranslationDto> getTranslation(@PathVariable Long id) {
        return translationService.getTranslation(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }
    
    @DeleteMapping("/delete/{id}")
    @Operation(summary = "Delete a translation")
    public ResponseEntity<Void> deleteTranslation(@PathVariable Long id) {
        translationService.deleteTranslation(id);
        return ResponseEntity.noContent().build();
    }
    
    @GetMapping("/search")
    @Operation(summary = "Search translations by key, content, locale, or tags")
    public ResponseEntity<Page<TranslationDto>> searchTranslations(
            @Parameter(description = "Search by key") @RequestParam(required = false) String key,
            @Parameter(description = "Search by content") @RequestParam(required = false) String content,
            @Parameter(description = "Filter by locale") @RequestParam(required = false) String locale,
            @Parameter(description = "Filter by tags") @RequestParam(required = false) List<String> tags,
            @Parameter(description = "Page number") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "20") int size) {
        
        Page<TranslationDto> results = translationService.searchTranslations(key, content, locale, tags, page, size);
        return ResponseEntity.ok(results);
    }
    
    @GetMapping("/export/{locale}")
    @Operation(summary = "Export translations for a locale as JSON")
    public ResponseEntity<Map<String, String>> exportTranslations(
            @Parameter(description = "Locale to export") @PathVariable String locale) {
        Map<String, String> translations = translationService.getTranslationsForLocale(locale);
        return ResponseEntity.ok(translations);
    }
    
    @GetMapping("/locales")
    @Operation(summary = "Get all available locales")
    public ResponseEntity<List<String>> getAvailableLocales() {
        List<String> locales = translationService.getAvailableLocales();
        return ResponseEntity.ok(locales);
    }
}