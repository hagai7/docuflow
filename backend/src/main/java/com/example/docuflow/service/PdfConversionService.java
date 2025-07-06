package com.example.docuflow.service;

import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.io.source.ByteArrayOutputStream;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.UUID;
import java.util.function.Consumer;

@Service
public class PdfConversionService {

    private static final int FONT_SIZE = 12;

    /**
     * Converts plain text file bytes to a PDF and reports progress.
     * Returns the generated PDF as a byte array.
     */
    public byte[] convertFileBytesToPdf(byte[] fileBytes, Consumer<Integer> progressCallback) throws IOException {
        System.out.println("Starting iText PDF conversion...");

        String text = new String(fileBytes, StandardCharsets.UTF_8);
        String[] lines = text.split("\r?\n");
        int totalLines = lines.length;

        // Output stream to hold the generated PDF bytes in memory
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        // Use try-with-resources to ensure PDF resources are closed properly
        try (PdfWriter writer = new PdfWriter(outputStream); // PdfWriter writes the PDF bytes to the output stream (e.g., file, memory)

            // PdfDocument manages the PDF structure (pages, metadata, objects)
            // It uses the PdfWriter to output the final bytes
            PdfDocument pdf = new PdfDocument(writer);

            // Document is a high-level layout API on top of PdfDocument
            // It lets you add paragraphs, tables, images easily without dealing with low-level PDF objects
            Document document = new Document(pdf)) {

            PdfFont font;
            try {
                font = PdfFontFactory.createFont("fonts/NotoSans-Regular.ttf", PdfFontFactory.EmbeddingStrategy.PREFER_EMBEDDED);
            } catch (Exception e) {
                font = PdfFontFactory.createFont(StandardFonts.HELVETICA);
            }
            document.setFont(font).setFontSize(FONT_SIZE);

            for (int i = 0; i < totalLines; i++) {
                document.add(new Paragraph(lines[i]));

                int progressPercent = getProgressPercent(i, totalLines);

                // Report progress to the callback function
                progressCallback.accept(progressPercent);
            }
        }

        progressCallback.accept(100);
        return outputStream.toByteArray();
    }

    public String generatePdfFileName(String originalFilename) {
        String baseName = originalFilename != null
                ? originalFilename.replaceAll("\\.[^.]+$", "")  // remove extension
                : "converted";

        // Format timestamp without characters invalid in filenames like colon ":"
        String timestamp = DateTimeFormatter.ISO_LOCAL_DATE_TIME
                .format(Instant.now().atZone(java.time.ZoneId.systemDefault()))
                .replace(":", "-");

        // Short UUID suffix for uniqueness
        String shortUuid = UUID.randomUUID().toString().substring(0, 8);

        return String.format("%s_converted_%s_%s.pdf", baseName, timestamp, shortUuid);
    }

    private int getProgressPercent(int i, int totalLines) {
        return (int) (((i + 1) / (double) totalLines) * 100);
    }
}
