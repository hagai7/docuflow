package com.example.docuflow.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;

import java.io.IOException;
import java.time.Duration;

@Service
public class S3StorageService {

    private final S3Client s3Client;
    private final S3Presigner s3Presigner;
    private final String bucketName;
    private static final int URL_DURATION_MINUTES = 15;

    public S3StorageService(S3Client s3Client,
                            S3Presigner s3Presigner,
                            @Value("${cloud.aws.s3.bucket}") String bucketName) {
        this.s3Client = s3Client;
        this.s3Presigner = s3Presigner;
        this.bucketName = bucketName;
    }

    /**
     * Uploads a PDF file to S3.
     *
     * @param key       unique file key
     * @param fileBytes byte content of the file
     * @throws IOException if upload fails
     */
    public void uploadFile(String key, byte[] fileBytes) throws IOException {
        PutObjectRequest request = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .contentType("application/pdf")
                .build();

        try {
            s3Client.putObject(request, RequestBody.fromBytes(fileBytes));
        } catch (S3Exception e) {
            throw new IOException("Failed to upload file to S3", e);
        }
    }

    /**
     * Generates a time-limited presigned URL for a file.
     *
     * @param key file key
     * @return presigned URL as string
     */
    public String generatePresignedUrl(String key) {
        GetObjectRequest getRequest = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build();

        GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(URL_DURATION_MINUTES))
                .getObjectRequest(getRequest)
                .build();

        return s3Presigner.presignGetObject(presignRequest).url().toString();
    }
}
