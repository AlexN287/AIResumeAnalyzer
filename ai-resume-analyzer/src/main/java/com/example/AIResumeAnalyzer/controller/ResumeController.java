package com.example.AIResumeAnalyzer.controller;

import com.example.AIResumeAnalyzer.Utils.PDFUtils;
import com.example.AIResumeAnalyzer.model.Resume;
import com.example.AIResumeAnalyzer.model.UploadedFile;
import com.example.AIResumeAnalyzer.service.ResumeService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/api/resumes")
public class ResumeController {
    private final ResumeService resumeService;

    public ResumeController(ResumeService resumeService) {
        this.resumeService = resumeService;
    }

    @PostMapping("/upload")
    public ResponseEntity<?> uploadResume(
            @RequestParam("userId") Long userId,
            @RequestParam("file") MultipartFile file) {
        try {
            if (file.isEmpty()) {
                return ResponseEntity.badRequest().body("File is empty");
            }

            Resume resume = resumeService.uploadResume(userId, file);

            return ResponseEntity.ok(Map.of(
                    "resumeId", resume.getId(),
                    "fileName", resume.getFileName(),
                    "uploadDate", resume.getUploadDate()
            ));

        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to upload resume");
        }
    }

    @GetMapping("/{userId}/pdf")
    public ResponseEntity<byte[]> getResumePdf(@PathVariable Long userId) {
        try {
            UploadedFile file = resumeService.getResumeFileByUserId(userId);
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "inline; filename=\"" + file.getFileName() + "\"")
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(file.getData());
        } catch (RuntimeException e) {
            if ("Resume not found".equals(e.getMessage())) {
                return ResponseEntity.notFound().build();
            }
            throw e;
        }
    }

}
