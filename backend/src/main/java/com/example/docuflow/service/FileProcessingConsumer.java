package com.example.docuflow.service;

import com.example.docuflow.config.RabbitConfig;
import com.example.docuflow.dto.FileMessage;
import jakarta.annotation.PreDestroy;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.Instant;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
public class FileProcessingConsumer {

    private final ExecutorService executor;
    private final PdfConversionService pdfConversionService;
    private final FileStorageService fileStorageService;
    private final ProgressWebSocketHandler progressWebSocketHandler;

    public FileProcessingConsumer(PdfConversionService pdfConversionService,
                                  FileStorageService fileStorageService,
                                  ProgressWebSocketHandler progressWebSocketHandler,
                                  @Value("${file.processor.threadCount:2}") int nThreads) {
        this.pdfConversionService = pdfConversionService;
        this.fileStorageService = fileStorageService;
        this.progressWebSocketHandler = progressWebSocketHandler;
        this.executor = Executors.newFixedThreadPool(nThreads);
    }

    @RabbitListener(queues = RabbitConfig.FILE_QUEUE)
    public void receiveMessage(FileMessage fileMessage) {
        executor.submit(() -> {
            String fileId = fileMessage.getFileId();
            try {
                byte[] pdfBytes = pdfConversionService.convertFileBytesToPdf(
                        fileMessage.getFileBytes(),
                        percent -> progressWebSocketHandler.sendToAll(
                                String.format("{\"fileId\":\"%s\",\"phase\":\"conversion\",\"percent\":%d}", fileId, percent)
                        )
                );

                progressWebSocketHandler.sendToAll(
                        String.format("{\"fileId\":\"%s\",\"phase\":\"saving\",\"percent\":0}", fileId)
                );

                fileStorageService.saveFile(
                        pdfBytes,
                        fileId,
                        "Converted File " + Instant.now()
                );

                progressWebSocketHandler.sendToAll(
                        String.format("{\"fileId\":\"%s\",\"phase\":\"saving\",\"percent\":100}", fileId)
                );

                progressWebSocketHandler.sendToAll(
                        String.format("{\"fileId\":\"%s\",\"phase\":\"done\",\"percent\":100}", fileId)
                );

                System.out.println("File processed and saved with ID: " + fileId);

            } catch (IOException e) {
                progressWebSocketHandler.sendToAll(
                        String.format("{\"fileId\":\"%s\",\"phase\":\"error\",\"message\":\"%s\"}",
                                fileId, e.getMessage())
                );
                throw new RuntimeException("Failed to process file " + fileId, e);
            }
        });

        System.out.println("Submitted processing task for: " + fileMessage.getFileName());
    }

    @PreDestroy
    public void shutdownExecutor() {
        executor.shutdown();
    }
}
