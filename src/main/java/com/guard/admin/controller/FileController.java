package com.guard.admin.controller;

import io.swagger.v3.oas.annotations.Operation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.guard.admin.database.repositories.GuardRepository;
import com.guard.admin.database.repositories.PhotoRepository;
import com.guard.admin.database.repositories.SiteRepository;
import com.guard.admin.service.declaration.AuthService;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("api/images")
public class FileController {

    @Autowired
    AuthService authService;

    @Autowired
    SiteRepository siteRepository;

    @Autowired
    GuardRepository guardRepository;

    @Autowired
    PhotoRepository photoRepository;

    private final String uploadDir = System.getProperty("user.dir") + File.separator + "upload/public/images";
    @Operation(summary = "Image View",
            description = "Can view Images using file name", tags = {"Image View"})
    @GetMapping("/{filename}")
    public ResponseEntity<byte[]> getImage(@PathVariable String filename) {
        try {
            Path filePath = Paths.get(uploadDir + File.separator + filename);
            byte[] image = Files.readAllBytes(filePath);

            String mimeType = Files.probeContentType(filePath);
            if (mimeType == null) {
                mimeType = "application/octet-stream";
            }

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType(mimeType));

            return new ResponseEntity<>(image, headers, HttpStatus.OK);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }


    

}
