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
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ResumeAnalysisImpl implements ResumeAnalysisService {
    private final ResumeAnalysisRepository resumeAnalysisRepository;
    private final ResumeRepository resumeRepository;
    private final UploadedFileRepository uploadedFileRepository;

    public ResumeAnalysisImpl(ResumeAnalysisRepository resumeAnalysisRepository, ResumeRepository resumeRepository, UploadedFileRepository uploadedFileRepository) {
        this.resumeAnalysisRepository = resumeAnalysisRepository;
        this.resumeRepository = resumeRepository;
        this.uploadedFileRepository = uploadedFileRepository;
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
    }
}
