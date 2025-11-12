package com.example.AIResumeAnalyzer.service.implementations;

import com.example.AIResumeAnalyzer.repository.ResumeRepository;
import com.example.AIResumeAnalyzer.service.ResumeService;
import org.springframework.stereotype.Service;

@Service
public class ResumeServiceImpl implements ResumeService {
    private final ResumeRepository resumeRepository;

    public ResumeServiceImpl(ResumeRepository resumeRepository) {
        this.resumeRepository = resumeRepository;
    }
}
