package com.example.AIResumeAnalyzer.mapper;

import com.example.AIResumeAnalyzer.dto.ResumeAnalysisDTO;
import com.example.AIResumeAnalyzer.model.Resume;
import com.example.AIResumeAnalyzer.model.ResumeAnalysis;

public class ResumeAnalysisMapper {
    public static ResumeAnalysisDTO toDTO(ResumeAnalysis analysis) {
        if (analysis == null) {
            return null;
        }

        Resume resume = analysis.getResume();

        return new ResumeAnalysisDTO(
                analysis.getId(),
                resume.getId(),
                resume.getUploadDate(),
                analysis.getStrengths(),
                analysis.getWeaknesses(),
                analysis.getSkillSuggestions(),
                analysis.getOverallFeedback(),
                resume.getFileName()
        );
    }
}
