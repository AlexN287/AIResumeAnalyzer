package com.example.AIResumeAnalyzer.service.implementations;

import com.example.AIResumeAnalyzer.dto.ResumeAnalysisDTO;
import com.example.AIResumeAnalyzer.mapper.ResumeAnalysisMapper;
import com.example.AIResumeAnalyzer.model.Resume;
import com.example.AIResumeAnalyzer.model.ResumeAnalysis;
import com.example.AIResumeAnalyzer.model.UploadedFile;
import com.example.AIResumeAnalyzer.repository.ResumeAnalysisRepository;
import com.example.AIResumeAnalyzer.repository.ResumeRepository;
import com.example.AIResumeAnalyzer.repository.UploadedFileRepository;
import com.example.AIResumeAnalyzer.service.ResumeAnalysisService;
import com.example.AIResumeAnalyzer.service.S3StorageService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ResumeAnalysisImpl implements ResumeAnalysisService {
    private final ResumeAnalysisRepository resumeAnalysisRepository;
    private final ResumeRepository resumeRepository;
    private final UploadedFileRepository uploadedFileRepository;
    private final S3StorageService s3StorageService;

    public ResumeAnalysisImpl(ResumeAnalysisRepository resumeAnalysisRepository, ResumeRepository resumeRepository, UploadedFileRepository uploadedFileRepository, S3StorageService s3StorageService) {
        this.resumeAnalysisRepository = resumeAnalysisRepository;
        this.resumeRepository = resumeRepository;
        this.uploadedFileRepository = uploadedFileRepository;
        this.s3StorageService = s3StorageService;
    }

    @Override
    @Transactional
    public List<ResumeAnalysisDTO> getAnalysesByUserId(Long userId) {
        return resumeAnalysisRepository.findByUserId(userId).stream()
                .map(ResumeAnalysisMapper::toDTO)
                .toList();
    }

    @Transactional
    @Override
    public void deleteResumeAnalysis(Long resumeAnalysisId) {
        ResumeAnalysis analysis = resumeAnalysisRepository.findById(resumeAnalysisId)
                .orElseThrow(() -> new RuntimeException("ResumeAnalysis not found"));

        Resume resume = analysis.getResume();
        UploadedFile uploadedFile = resume.getUploadedFile();

        resumeAnalysisRepository.delete(analysis);
        resumeRepository.delete(resume);
        uploadedFileRepository.delete(uploadedFile);

        // Delete the PDF from S3 after the DB records are removed
        if (uploadedFile.getS3Key() != null) {
            s3StorageService.deleteFile(uploadedFile.getS3Key());
        }
    }
}
