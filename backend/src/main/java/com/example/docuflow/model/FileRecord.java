package com.example.docuflow.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.Instant;

@Data
@Entity
@Table(name = "files")
public class FileRecord {

    @Id
    private String id;  // UUID string ID
    // file id is a UUID string so it can be used as a globally unique identifier across systems

    @Column(nullable = false)
    @NotBlank
    @Size(max = 255)
    private String filename;

    @Column(nullable = false)
    private Long totalSize;

    // Renamed for clarity: the time record was created (initial upload request time)
    @Column(nullable = false)
    private Instant createdAt;

    // Time when processing finished / file was saved
    @Column
    private Instant completedAt;

    // The S3 key (path) where the file is stored in AWS S3
    @Column
    private String s3Key;

    // Status: PROCESSING or COMPLETED
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FileStatus status;

    // Method called automatically before the entity is persisted (saved) for the first time
    @PrePersist
    public void prePersist() {
        if (createdAt == null) {
            createdAt = Instant.now();
        }
        if (status == null) {
            status = FileStatus.PROCESSING;
        }
        if (totalSize == null) {
            totalSize = 0L;
        }
    }
}
