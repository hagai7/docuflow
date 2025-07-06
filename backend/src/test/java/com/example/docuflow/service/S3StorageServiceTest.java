package com.example.docuflow.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

import java.io.IOException;
import java.net.URL;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class S3StorageServiceTest {

    private S3Client s3Client;
    private S3Presigner s3Presigner;
    private S3StorageService s3StorageService;

    @BeforeEach
    void setup() {
        s3Client = mock(S3Client.class);
        s3Presigner = mock(S3Presigner.class);
        s3StorageService = new S3StorageService(s3Client, s3Presigner, "test-bucket");
    }

    @Test
    void testUploadFile_callsS3Client() throws IOException {
        byte[] fileBytes = "Test PDF".getBytes();

        // Act
        s3StorageService.uploadFile("test.pdf", fileBytes);

        // Assert
        verify(s3Client, times(1)).putObject(any(PutObjectRequest.class), any(RequestBody.class));
    }

    @Test
    void testGeneratePresignedUrl_returnsUrl() {
        // Mock URL
        PresignedGetObjectRequest presignedRequest = mock(PresignedGetObjectRequest.class);
        when(presignedRequest.url()).thenReturn(mock(URL.class));
        when(s3Presigner.presignGetObject(any(GetObjectPresignRequest.class)))
                .thenReturn(presignedRequest);

        // Act
        String url = s3StorageService.generatePresignedUrl("test-key");

        // Assert
        assertNotNull(url);
        verify(s3Presigner, times(1)).presignGetObject(any(GetObjectPresignRequest.class));
    }
}
