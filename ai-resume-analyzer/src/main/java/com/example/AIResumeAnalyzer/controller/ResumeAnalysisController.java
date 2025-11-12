package com.example.AIResumeAnalyzer.controller;

import com.example.AIResumeAnalyzer.service.ResumeAnalysisService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/resume-analysis")
public class ResumeAnalysisController {
    private final ResumeAnalysisService resumeAnalysisService;

    public ResumeAnalysisController(ResumeAnalysisService resumeAnalysisService) {
        this.resumeAnalysisService = resumeAnalysisService;
    }
}
