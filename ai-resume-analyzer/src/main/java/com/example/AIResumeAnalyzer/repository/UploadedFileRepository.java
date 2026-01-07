package com.example.AIResumeAnalyzer.repository;

import com.example.AIResumeAnalyzer.model.UploadedFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UploadedFileRepository extends JpaRepository<UploadedFile, Long> {
}
