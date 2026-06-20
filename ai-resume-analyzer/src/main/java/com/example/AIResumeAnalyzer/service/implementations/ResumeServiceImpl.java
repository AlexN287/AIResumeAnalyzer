package com.example.AIResumeAnalyzer.service.implementations;

import com.example.AIResumeAnalyzer.model.Resume;
import com.example.AIResumeAnalyzer.model.ResumeAnalysis;
import com.example.AIResumeAnalyzer.model.UploadedFile;
import com.example.AIResumeAnalyzer.model.User;
import com.example.AIResumeAnalyzer.openAI.OpenAIservice;
import com.example.AIResumeAnalyzer.repository.ResumeAnalysisRepository;
import com.example.AIResumeAnalyzer.repository.ResumeRepository;
import com.example.AIResumeAnalyzer.repository.UserRepository;
import com.example.AIResumeAnalyzer.service.ResumeService;
import com.example.AIResumeAnalyzer.service.S3StorageService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ResumeServiceImpl implements ResumeService {

    private final ResumeRepository resumeRepository;
    private final UserRepository userRepository;
    private final OpenAIservice openAIservice;
    private final ResumeAnalysisRepository resumeAnalysisRepository;
    private final S3StorageService s3StorageService;

    @Transactional
    @Override
    public Resume uploadResume(Long userId, MultipartFile file) throws IOException {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        byte[] fileBytes = file.getBytes();

        // Build a unique S3 key: resumes/{userId}/{uuid}_{originalFileName}
        String s3Key = "resumes/" + userId + "/" + UUID.randomUUID() + "_" + file.getOriginalFilename();

        // Upload PDF to S3
        s3StorageService.uploadFile(s3Key, fileBytes, file.getContentType());

        // Persist only metadata + S3 key — no binary data in the DB
        UploadedFile uploadedFile = new UploadedFile();
        uploadedFile.setFileName(file.getOriginalFilename());
        uploadedFile.setContentType(file.getContentType());
        uploadedFile.setS3Key(s3Key);

        Resume resume = new Resume(
                user,
                file.getOriginalFilename(),
                uploadedFile,
                LocalDate.now()
        );

        resume = resumeRepository.save(resume);

        // Call OpenAI
        ResumeAnalysis analysis = openAIservice.analyzeResume(fileBytes, resume);

        resumeAnalysisRepository.save(analysis);

        resume.setResumeAnalysis(analysis);

        return resume;
    }

    @Transactional
    @Override
    public UploadedFile getResumeFileByUserId(Long userId) {
        Resume resume = resumeRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Resume not found"));

        return fetchBytesFromS3(resume.getUploadedFile());
    }

    @Transactional
    @Override
    public UploadedFile getResumeFileByResumeId(Long resumeId) {
        Resume resume = resumeRepository.findById(resumeId)
                .orElseThrow(() -> new RuntimeException("Resume not found"));

        return fetchBytesFromS3(resume.getUploadedFile());
    }

    /** Populates the transient {@code data} field by downloading the file from S3. */
    private UploadedFile fetchBytesFromS3(UploadedFile uploadedFile) {
        byte[] bytes = s3StorageService.downloadFile(uploadedFile.getS3Key());
        uploadedFile.setData(bytes);
        return uploadedFile;
    }
}
