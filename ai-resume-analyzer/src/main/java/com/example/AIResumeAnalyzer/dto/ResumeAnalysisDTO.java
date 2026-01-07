package com.example.AIResumeAnalyzer.dto;

import java.time.LocalDate;

public record ResumeAnalysisDTO(Long id,
                                Long resumeId,
                                LocalDate uploadDate,
                                String strengths,
                                String weaknesses,
                                String skillSuggestions,
                                String overallFeedback,
                                String fileName) {
}
