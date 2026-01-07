package com.example.AIResumeAnalyzer.service;

import com.example.AIResumeAnalyzer.dto.ResumeAnalysisDTO;

import java.util.List;

public interface ResumeAnalysisService {
    List<ResumeAnalysisDTO> getAnalysesByUserId(Long userId);
    void deleteResumeAnalysis(Long resumeAnalysisId);
}
