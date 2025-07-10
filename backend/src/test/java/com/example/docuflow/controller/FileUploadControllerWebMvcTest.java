package com.example.docuflow.controller;

import com.example.docuflow.security.JwtUtil;
import com.example.docuflow.service.FileStorageService;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(FileUploadController.class)
@AutoConfigureMockMvc(addFilters = false)
public class FileUploadControllerWebMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RabbitTemplate rabbitTemplate;

    @MockBean
    private FileStorageService fileStorageService;

    @MockBean
    private JwtUtil jwtUtil; // Added to prevent bean-not-found error

    @Test
    void testUploadFile() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file", "file.txt", "text/plain", "hello world".getBytes());

        when(fileStorageService.createFileRecord(any())).thenReturn("file123");

        mockMvc.perform(multipart("/api/files/upload").file(file))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("file123"))
                .andExpect(jsonPath("$.message").value("File upload started"));
    }
}
