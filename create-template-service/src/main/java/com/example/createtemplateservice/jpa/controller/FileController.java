package com.example.createtemplateservice.jpa.controller;

import java.io.IOException;

import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.example.createtemplateservice.utils.FileUploadUtil;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class FileController {

    private final FileUploadUtil fileStorageService;

    @GetMapping("/downloadFile/{id}/{fileName:.+}")
    public ResponseEntity<Resource> downloadFile(@PathVariable Long id, @PathVariable String fileName,
            HttpServletRequest request) {
        // Load file as Resource
        Resource resource = fileStorageService.loadFileAsResource(fileName, id);

        // Try to determine file's content type
        String contentType = null;
        try {
            contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
        } catch (IOException ex) {
            log.info("Could not determine file type.");
        }

        // Fallback to the default content type if type could not be determined
        if (contentType == null) {
            contentType = "application/octet-stream";
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }

    /*
     * @DeleteMapping("/files/{userId}/{filename:.+}")
     * public ResponseEntity<CustomErrorResponse> deleteFile(@PathVariable Long
     * userId, @PathVariable String filename) {
     * String message = "";
     * 
     * try {
     * boolean existed = fileStorageService.delete(filename, userId);
     * 
     * if (existed) {
     * message = "File deleted successfully: '" + filename + "'";
     * return ResponseEntity.status(HttpStatus.OK).body(new
     * CustomErrorResponse(message));
     * }
     * 
     * message = "The file does not exist!";
     * return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new
     * CustomErrorResponse(message));
     * } catch (Exception e) {
     * message = "Could not delete the file: " + filename + ". Error: " +
     * e.getMessage();
     * return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new
     * CustomErrorResponse(message));
     * }
     * }
     */
}