package com.translation.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.translation.dto.TranslationDto;
import com.translation.service.TranslationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import com.translation.security.JwtUtil;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.*;
import java.util.HashSet;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class TranslationControllerTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @MockBean
    private TranslationService translationService;
    
    @MockBean
    private JwtUtil jwtUtil;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    private TranslationDto translationDto;
    
    @BeforeEach
    void setUp() {
        translationDto = new TranslationDto("button.save", "en", "Save", new HashSet<>(Set.of("mobile")));
        translationDto.setId(1L);
    }
    
    @Test
    @WithMockUser
    void createTranslation_Success() throws Exception {
        when(translationService.createTranslation(any(TranslationDto.class))).thenReturn(translationDto);
        
        mockMvc.perform(post("/api/translations/create")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(translationDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.key").value("button.save"))
                .andExpect(jsonPath("$.locale").value("en"))
                .andExpect(jsonPath("$.content").value("Save"));
    }
    
    @Test
    @WithMockUser
    void getTranslation_Success() throws Exception {
        when(translationService.getTranslation(1L)).thenReturn(Optional.of(translationDto));
        mockMvc.perform(get("/api/translations/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.key").value("button.save"));
    }
    
    @Test
    @WithMockUser
    void getTranslation_NotFound() throws Exception {
        when(translationService.getTranslation(1L)).thenReturn(Optional.empty());
        mockMvc.perform(get("/api/translations/1"))
                .andExpect(status().isNotFound());
    }

    
    @Test
    @WithMockUser
    void exportTranslations_Success() throws Exception {
        Map<String, String> translations = Map.of("button.save", "Save");
        when(translationService.getTranslationsForLocale("en")).thenReturn(translations);
        mockMvc.perform(get("/api/translations/export/en"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$['button.save']").value("Save"));
    }
    
    @Test
    void createTranslation_Unauthorized() throws Exception {
        mockMvc.perform(post("/api/translations/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(translationDto)))
                .andExpect(status().isForbidden());
    }
}