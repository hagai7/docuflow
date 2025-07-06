package com.example.docuflow.controller;

import com.example.docuflow.config.RabbitConfig;
import com.example.docuflow.dto.FileMessage;
import com.example.docuflow.dto.FileUploadResponse;
import com.example.docuflow.service.FileStorageService;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping("/api/files")
public class FileUploadController {

    private final RabbitTemplate rabbitTemplate;
    private final FileStorageService fileStorageService;

    public FileUploadController(RabbitTemplate rabbitTemplate, FileStorageService fileStorageService) {
        this.rabbitTemplate = rabbitTemplate;
        this.fileStorageService = fileStorageService;
    }

    @PostMapping("/upload")
    public ResponseEntity<FileUploadResponse> uploadFile(@RequestParam("file") MultipartFile file) throws Exception {
        // Create DB record with status PROCESSING and generate a unique fileId
        // This allows tracking the file upload and processing asynchronously - so frontend knows which file belongs to which progress
        String fileId = fileStorageService.createFileRecord("Uploading File " + Instant.now());
        byte[] fileBytes = file.getBytes();

        // original file name is file.txt and not file name from html tag <input type="file" name="file">

        rabbitTemplate.convertAndSend(RabbitConfig.FILE_QUEUE, new FileMessage(fileId, file.getOriginalFilename(), fileBytes));

        // Return the fileId immediately in the response to allow the client to query status later
        // You can add fields like upload progress or any initial info as needed
        return ResponseEntity.ok(new FileUploadResponse(fileId, "File upload started", 0, 0, List.of()));
    }

}
