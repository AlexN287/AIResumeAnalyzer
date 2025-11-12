package com.example.AIResumeAnalyzer.repository;

import com.example.AIResumeAnalyzer.model.Resume;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ResumeRepository extends JpaRepository<Resume, Long> {
}
