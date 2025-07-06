package com.example.docuflow.controller;

import com.example.docuflow.model.FileRecord;
import com.example.docuflow.model.FileStatus;
import com.example.docuflow.repository.FileRepository;
import com.example.docuflow.service.S3StorageService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/files")
public class FileDownloadController {

    private final FileRepository fileRepository;
    private final S3StorageService s3StorageService;

    public FileDownloadController(FileRepository fileRepository, S3StorageService s3StorageService) {
        this.fileRepository = fileRepository;
        this.s3StorageService = s3StorageService;
    }

    /**
     * Returns a presigned URL for downloading a file if ready,
     * or a status message if the file is still processing or not found.
     */
    @GetMapping("/{fileId}")
    public ResponseEntity<Map<String, String>> getDownloadUrl(@PathVariable String fileId) {
        Optional<FileRecord> optionalFileRecord = fileRepository.findById(fileId);
        if (optionalFileRecord.isEmpty()) {
            return ResponseEntity.status(404)
                    .body(Map.of("message", "File not found"));
        }
        FileRecord fileRecord = optionalFileRecord.get();
        if (fileRecord.getStatus().equals(FileStatus.PROCESSING)) {
            return ResponseEntity.status(202)
                    .body(Map.of("message", "File is still being processed. Please try again later."));
        }

        String presignedUrl = s3StorageService.generatePresignedUrl(fileRecord.getS3Key());
        return ResponseEntity.ok(Map.of("url", presignedUrl));
    }
}
