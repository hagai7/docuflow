package com.example.docuflow.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FileMessage implements Serializable {
    private String fileId;
    private String fileName;
    private byte[] fileBytes;

}