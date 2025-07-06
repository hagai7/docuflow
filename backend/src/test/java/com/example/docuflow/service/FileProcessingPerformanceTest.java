package com.example.docuflow.service;

import com.example.docuflow.dto.FileMessage;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.concurrent.TimeUnit;

public class FileProcessingPerformanceTest {

    @Test
    void benchmarkMultiThreadedProcessingDifferentThreads() throws Exception {
        FileStorageService storage = Mockito.mock(FileStorageService.class);
        PdfConversionService pdf = Mockito.mock(PdfConversionService.class);
        ProgressWebSocketHandler ws = Mockito.mock(ProgressWebSocketHandler.class);

        int[] threadCounts = {1, 2, 4};

        for (int nThreads : threadCounts) {
            FileProcessingConsumer consumer = new FileProcessingConsumer(pdf, storage, ws, nThreads);

            long start = System.currentTimeMillis();

            for (int i = 0; i < 10; i++) {
                FileMessage msg = new FileMessage("file" + i, "file" + i + ".txt", "Data".getBytes());
                consumer.receiveMessage(msg);
            }

            TimeUnit.SECONDS.sleep(3); // Wait for processing to finish

            long duration = System.currentTimeMillis() - start;
            System.out.println("Threads: " + nThreads + " | Total time for 10 files: " + duration + " ms");

            consumer.shutdownExecutor();
        }
    }
}
