package com.example.createtemplateservice.jpa.controller;

import com.example.createtemplateservice.exceptions.ResourceFoundException;
import com.example.createtemplateservice.exceptions.ResourceNotFoundException;
import com.example.createtemplateservice.jpa.dto.request.DictionaryRequest;
import com.example.createtemplateservice.jpa.dto.response.DictionaryResponse;
import com.example.createtemplateservice.jpa.service.interfaces.DictionaryService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class DictionaryController {

    private final DictionaryService dictionaryService;

    @PostMapping("/saveDictionary")
    public ResponseEntity<Object> saveDictionary(@Valid @RequestBody DictionaryRequest dictionaryRequest) throws ResourceFoundException {
        URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/saveDictionary").toUriString());
        DictionaryResponse savedDictionary = dictionaryService.saveDictionary(dictionaryRequest);
        return ResponseEntity.created(uri).body(savedDictionary);
    }

    @GetMapping("/getAllDictionary")
    public ResponseEntity<Object> getAllDictionary(@Pattern(regexp = "uom") @RequestParam(required = false) String show) {
        List<?> allDictionary;
        if (show == null) {
            allDictionary = dictionaryService.getAllDictionary(show);
        } else if (show.equals("uom")) {
            allDictionary = dictionaryService.getAllDictionaryNmUom(show);
        } else {
            allDictionary = dictionaryService.getAllDictionary(show);

        }
        return ResponseEntity.ok(allDictionary);
    }


    @GetMapping("/getDictionaryById/{id}")
    public ResponseEntity<Object> getDictionaryById(@PathVariable Long id, @Pattern(regexp = "uom") @RequestParam(required = false) String show) throws ResourceNotFoundException {
        Object dictionaryById;
        if (show == null) {
            dictionaryById = dictionaryService.getDictionaryById(id, show);
        } else if (show.equals("uom")) {
            dictionaryById = dictionaryService.getDictionaryNmUomById(id, show);
        } else {
            dictionaryById = dictionaryService.getDictionaryById(id, show);
        }
        return ResponseEntity.ok(dictionaryById);
    }

    @GetMapping("/noun-suggestions")
    public ResponseEntity<Object> getNounSuggestions(@RequestParam String noun) {
        List<String> nouns = dictionaryService.getNounSuggestions(noun);
        return ResponseEntity.ok(nouns);
    }

    @GetMapping("/modifier-suggestions")
    public ResponseEntity<Object> getModifierSuggestions(@RequestParam String noun) {
        List<String> modifiers = dictionaryService.getModifiersByNoun(noun);
        return ResponseEntity.ok(modifiers);
    }

    @PutMapping("/updateDictionary/{id}")
    public ResponseEntity<Object> updateDictionary(@PathVariable Long id, @Valid @RequestBody DictionaryRequest updateDictionaryRequest) throws ResourceNotFoundException, ResourceFoundException {
        DictionaryResponse updateDictionary = dictionaryService.updateDictionary(id, updateDictionaryRequest);
        return ResponseEntity.ok(updateDictionary);
    }

    @DeleteMapping("/deleteDictionary/{id}")
    public ResponseEntity<Object> deleteDictionary(@PathVariable Long id) throws ResourceNotFoundException {
        dictionaryService.deleteDictionaryId(id);
        return ResponseEntity.noContent().build();
    }

}
