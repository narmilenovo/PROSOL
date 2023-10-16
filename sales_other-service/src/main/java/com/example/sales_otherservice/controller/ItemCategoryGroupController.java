package com.example.sales_otherservice.controller;

import com.example.sales_otherservice.dto.request.ItemCategoryGroupRequest;
import com.example.sales_otherservice.dto.response.ItemCategoryGroupResponse;
import com.example.sales_otherservice.exceptions.ResourceFoundException;
import com.example.sales_otherservice.exceptions.ResourceNotFoundException;
import com.example.sales_otherservice.service.interfaces.ItemCategoryGroupService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class ItemCategoryGroupController {
    private final ItemCategoryGroupService itemCategoryGroupService;

    @PostMapping("/saveIcg")
    public ResponseEntity<Object> saveIcg(@Valid @RequestBody ItemCategoryGroupRequest itemCategoryGroupRequest) throws ResourceFoundException {
        URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/saveIcg").toUriString());
        ItemCategoryGroupResponse saveIcg = itemCategoryGroupService.saveIcg(itemCategoryGroupRequest);
        return ResponseEntity.created(uri).body(saveIcg);
    }

    @GetMapping("/getAllIcg")
    public ResponseEntity<Object> getAllIcg() {
        List<ItemCategoryGroupResponse> allIcg = itemCategoryGroupService.getAllIcg();
        return ResponseEntity.ok(allIcg);
    }


    @GetMapping("/getIcgById/{id}")
    public ResponseEntity<Object> getIcgById(@PathVariable Long id) throws ResourceNotFoundException {
        ItemCategoryGroupResponse dpById = itemCategoryGroupService.getIcgById(id);
        return ResponseEntity.ok(dpById);
    }

    @GetMapping("/getAllIcgTrue")
    public ResponseEntity<Object> listIcgStatusTrue() {
        List<ItemCategoryGroupResponse> groupResponses = itemCategoryGroupService.findAllStatusTrue();
        return ResponseEntity.ok(groupResponses);
    }

    @PutMapping("/updateIcg/{id}")
    public ResponseEntity<Object> updateIcg(@PathVariable Long id, @Valid @RequestBody ItemCategoryGroupRequest updateItemCategoryGroupRequest) throws ResourceNotFoundException, ResourceFoundException {
        ItemCategoryGroupResponse updateIcg = itemCategoryGroupService.updateIcg(id, updateItemCategoryGroupRequest);
        return ResponseEntity.ok(updateIcg);
    }

    @DeleteMapping("/deleteIcg/{id}")
    public ResponseEntity<Object> deleteIcg(@PathVariable Long id) throws ResourceNotFoundException {
        itemCategoryGroupService.deleteIcgById(id);
        return ResponseEntity.noContent().build();
    }
}
