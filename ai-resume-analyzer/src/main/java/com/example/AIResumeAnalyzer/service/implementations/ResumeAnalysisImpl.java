package com.example.AIResumeAnalyzer.service.implementations;

import com.example.AIResumeAnalyzer.repository.ResumeAnalysisRepository;
import com.example.AIResumeAnalyzer.service.ResumeAnalysisService;
import org.springframework.stereotype.Service;

@Service
public class ResumeAnalysisImpl implements ResumeAnalysisService {
    private final ResumeAnalysisRepository resumeAnalysisRepository;

    public ResumeAnalysisImpl(ResumeAnalysisRepository resumeAnalysisRepository) {
        this.resumeAnalysisRepository = resumeAnalysisRepository;
    }
}
