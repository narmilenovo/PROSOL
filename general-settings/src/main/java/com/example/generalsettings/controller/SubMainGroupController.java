package com.example.generalsettings.controller;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.example.generalsettings.config.AlreadyExistsException;
import com.example.generalsettings.config.ResourceNotFoundException;
import com.example.generalsettings.request.SubMainGroupRequest;
import com.example.generalsettings.response.MainGroupCodesResponse;
import com.example.generalsettings.response.SubMainGroupResponse;
import com.example.generalsettings.service.MainGroupCodesService;
import com.example.generalsettings.service.SubMainGroupService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class SubMainGroupController {
	private final SubMainGroupService subMainGroupService;


    private final MainGroupCodesService mainGroupCodesService;

    @GetMapping("/getAllSubMainGroup")
    public ResponseEntity<Object> getAllSubMainGroup() {
        List<SubMainGroupResponse> subMainGroups = subMainGroupService.getAllSubMainGroup();
        return ResponseEntity.ok(subMainGroups);
    }

    @PutMapping("/updateSubMainGroup/{id}")
    public ResponseEntity<Object> updateSubMainGroup(@PathVariable Long id, @RequestBody SubMainGroupRequest subMainGroupRequest) throws ResourceNotFoundException, AlreadyExistsException {
        SubMainGroupResponse updatesubMainGroup = subMainGroupService.updateSubMainGroup(id, subMainGroupRequest);
        return ResponseEntity.ok().body(updatesubMainGroup);
    }

    @DeleteMapping("/deleteSubMainGroup/{id}")
    public ResponseEntity<String> deleteSubMainGroup(@PathVariable Long id) throws ResourceNotFoundException {
        subMainGroupService.deleteSubMainGroup(id);
        return ResponseEntity.ok().body("SubMainGroup of '" + id + "' is deleted");
    }

    @PostMapping("/saveSubMainGroup")
    public ResponseEntity<Object> saveSubMainGroup(@Valid @RequestBody SubMainGroupRequest subMainGroupRequest) throws ResourceNotFoundException, AlreadyExistsException {
        URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/saveSubMainGroup").toUriString());
        SubMainGroupResponse savedSubMainGroup = subMainGroupService.saveSubMainGroup(subMainGroupRequest);
        return ResponseEntity.created(uri).body(savedSubMainGroup);
    }

    @GetMapping("/getSubMainGroupById/{id}")
    public ResponseEntity<Object> getSubMainGroupById(@PathVariable Long id) throws ResourceNotFoundException {
        SubMainGroupResponse foundSubMainGroup = subMainGroupService.getSubMainGroupById(id);
        return ResponseEntity.ok(foundSubMainGroup);
    }

    @GetMapping("/getMainGroupAll")
    public ResponseEntity<Object> getMainGroupAll() {
        List<MainGroupCodesResponse> mainGroup = mainGroupCodesService.getAllMainGroupCodes();
        return ResponseEntity.ok(mainGroup);
    }

    @PatchMapping("/updateSubMainGroupStatusById/{id}")
    public ResponseEntity<Object> updateSubMainGroupStatusId(@PathVariable Long id) throws ResourceNotFoundException {
        SubMainGroupResponse response = subMainGroupService.updateStatusUsingSubMainGroupId(id);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/updateBulkStatusSubMainGrouptId/{id}")
    public ResponseEntity<Object> updateBulkStatusSubMainGroupId(@PathVariable List<Long> id) {
        List<SubMainGroupResponse> responseList = subMainGroupService.updateBulkStatusSubMainGroupId(id);
        return ResponseEntity.ok(responseList);
    }
}
