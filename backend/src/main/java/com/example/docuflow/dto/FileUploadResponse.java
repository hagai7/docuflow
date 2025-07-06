package com.example.docuflow.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class FileUploadResponse {
    private String id;
    private String message;
    private long totalSize;

    // Number of chunks the file was split into for upload or processing
    private int chunkCount;

    // List of hashes for each chunk, because hashes provide a unique identifier
    // based on the chunk’s content (unlike simple generated IDs), helping to track and manage chunks reliably.
    private List<String> hashesOrdered;
}
