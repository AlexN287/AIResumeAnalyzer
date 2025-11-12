package com.example.AIResumeAnalyzer.repository;

import com.example.AIResumeAnalyzer.model.ResumeAnalysis;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ResumeAnalysisRepository extends JpaRepository<ResumeAnalysis ,Long> {
}
