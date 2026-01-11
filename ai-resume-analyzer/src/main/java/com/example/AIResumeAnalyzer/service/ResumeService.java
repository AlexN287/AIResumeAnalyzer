package com.example.AIResumeAnalyzer.service;

import com.example.AIResumeAnalyzer.model.Resume;
import com.example.AIResumeAnalyzer.model.UploadedFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface ResumeService {
    Resume uploadResume(Long userId, MultipartFile file) throws IOException;
    UploadedFile getResumeFileByUserId(Long userId);
    UploadedFile getResumeFileByResumeId(Long resumeId);
}
