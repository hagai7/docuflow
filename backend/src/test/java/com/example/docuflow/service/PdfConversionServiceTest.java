package com.example.docuflow.service;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

public class PdfConversionServiceTest {

    private final PdfConversionService pdfConversionService = new PdfConversionService();

    @Test
    void testConvertFileBytesToPdf_progressCallback() throws IOException {
        String content = "Line1\nLine2\nLine3\n";
        byte[] inputBytes = content.getBytes();

        AtomicInteger lastProgress = new AtomicInteger(-1);

        // Save last progress percentage reported
        byte[] pdfBytes = pdfConversionService.convertFileBytesToPdf(inputBytes, lastProgress::set);

        assertNotNull(pdfBytes);
        assertTrue(pdfBytes.length > 0);
        assertEquals(100, lastProgress.get()); // Ensure progress callback reached 100%
    }

    @Test
    void testGeneratePdfFileName_nonNull() {
        String fileName = "example.txt";
        String pdfName = pdfConversionService.generatePdfFileName(fileName);

        assertTrue(pdfName.startsWith("example_converted_"));
        assertTrue(pdfName.endsWith(".pdf"));
    }
}
