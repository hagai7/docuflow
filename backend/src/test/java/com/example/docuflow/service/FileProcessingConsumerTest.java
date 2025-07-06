package com.example.docuflow.service;

import com.example.docuflow.dto.FileMessage;
import org.junit.jupiter.api.Test;

import java.util.concurrent.TimeUnit;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class FileProcessingConsumerTest {

    @Test
    void testReceiveMessage_callsDependenciesAndProcessesFile() throws Exception {
        PdfConversionService pdf = mock(PdfConversionService.class);
        FileStorageService storage = mock(FileStorageService.class);
        ProgressWebSocketHandler ws = mock(ProgressWebSocketHandler.class);

        // Mock pdfConversionService to return dummy PDF bytes
        when(pdf.convertFileBytesToPdf(any(), any())).thenReturn("pdfData".getBytes());

        FileProcessingConsumer consumer = new FileProcessingConsumer(pdf, storage, ws, 2);

        FileMessage msg = new FileMessage("file1", "file1.txt", "content".getBytes());

        consumer.receiveMessage(msg);

        // Wait a bit for async processing
        TimeUnit.SECONDS.sleep(1);

        verify(pdf).convertFileBytesToPdf(any(), any());
        verify(storage).saveFile(any(), eq("file1"), any());
        verify(ws, atLeastOnce()).sendToAll(any());
    }
}
