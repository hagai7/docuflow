package com.example.docuflow.controller;

import com.example.docuflow.config.RabbitConfig;
import com.example.docuflow.dto.FileUploadResponse;
import com.example.docuflow.dto.FileMessage;
import com.example.docuflow.service.FileStorageService;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class FileUploadControllerUnitTest {

    @Test
    void testUploadFile() throws Exception {
        RabbitTemplate rabbitTemplate = mock(RabbitTemplate.class);
        FileStorageService fileStorageService = mock(FileStorageService.class);

        FileUploadController controller = new FileUploadController(rabbitTemplate, fileStorageService);

        MockMultipartFile mockFile = new MockMultipartFile("file", "file.txt", "text/plain", "test content".getBytes());

        when(fileStorageService.createFileRecord(any())).thenReturn("file123");

        ResponseEntity<FileUploadResponse> response = controller.uploadFile(mockFile);

        assertNotNull(response.getBody());
        assertEquals("file123", response.getBody().getId());
        assertEquals("File upload started", response.getBody().getMessage());

        // rabbitTemplate.convertAndSend was called with first arg equal exactly to abbitConfig.FILE_QUEUE
        // and second arg is instance of FileMessage
        verify(rabbitTemplate).convertAndSend(eq(RabbitConfig.FILE_QUEUE), any(FileMessage.class));
    }
}
