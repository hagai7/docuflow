package com.example.docuflow.service;

import com.example.docuflow.model.FileRecord;
import com.example.docuflow.model.FileStatus;
import com.example.docuflow.repository.FileRepository;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
public class FileStorageService {

    private final S3StorageService s3StorageService;
    private final FileRepository fileRepository;

    public FileStorageService(S3StorageService s3StorageService, FileRepository fileRepository) {
        this.s3StorageService = s3StorageService;
        this.fileRepository = fileRepository;
    }

    // Create a new DB record for a file upload request (status=PROCESSING)
    public String createFileRecord(String filename) {
        String fileId = UUID.randomUUID().toString();
        FileRecord record = new FileRecord();
        record.setId(fileId);
        record.setFilename(filename);
        record.setStatus(FileStatus.PROCESSING);
        record.setCreatedAt(Instant.now());
        fileRepository.save(record);
        System.out.println("Created file record with ID: " + fileId + ", filename: " + filename);
        return fileId;
    }

    // After processing finishes, save file bytes to S3 and update DB record
    public void saveFile(byte[] fileBytes, String fileId, String filename) throws IOException {
        String key = "converted/" + fileId + ".pdf";
        s3StorageService.uploadFile(key, fileBytes);

        Optional<FileRecord> optRecord = fileRepository.findById(fileId);
        if (optRecord.isPresent()) {
            FileRecord record = optRecord.get();
            record.setS3Key(key);
            record.setTotalSize((long) fileBytes.length);
            record.setStatus(FileStatus.COMPLETED);
            record.setFilename(filename);
            record.setCompletedAt(Instant.now());
            fileRepository.save(record);
        } else {
            throw new IOException("FileRecord not found for id " + fileId);
        }
    }
}
