package com.example.AIResumeAnalyzer.controller;

import com.example.AIResumeAnalyzer.dto.ResumeAnalysisDTO;
import com.example.AIResumeAnalyzer.service.ResumeAnalysisService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/resume-analysis")
public class ResumeAnalysisController {
    private final ResumeAnalysisService resumeAnalysisService;

    public ResumeAnalysisController(ResumeAnalysisService resumeAnalysisService) {
        this.resumeAnalysisService = resumeAnalysisService;
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getResumeAnalysesByUser(@PathVariable Long userId) {
        List<ResumeAnalysisDTO> analyses = resumeAnalysisService.getAnalysesByUserId(userId);
        return ResponseEntity.ok(analyses);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteResumeAnalysis(@PathVariable Long id) {
        resumeAnalysisService.deleteResumeAnalysis(id);
        return ResponseEntity.noContent().build();
    }
}
