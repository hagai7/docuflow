package com.example.docuflow.repository;

import com.example.docuflow.model.FileRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FileRepository extends JpaRepository<FileRecord, String> {
}
