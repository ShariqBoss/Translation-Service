package com.translation.controller;

import com.translation.service.DataPopulationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/populate")
@Tag(name = "Data Population", description = "Database population for testing")
public class DataPopulationController {
    
    @Autowired
    private DataPopulationService dataPopulationService;
    
    @PostMapping("/translations")
    @Operation(summary = "Populate database with test translations")
    public ResponseEntity<String> populateTranslations(
            @Parameter(description = "Number of records to create") 
            @RequestParam(defaultValue = "100000") int count) {
        
        long startTime = System.currentTimeMillis();
        dataPopulationService.populateDatabase(count);
        long endTime = System.currentTimeMillis();
        
        String message = String.format("Successfully created %d translations in %d ms", 
                                     count, (endTime - startTime));
        return ResponseEntity.ok(message);
    }
}