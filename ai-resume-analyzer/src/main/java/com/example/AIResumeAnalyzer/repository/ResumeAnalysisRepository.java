package com.example.AIResumeAnalyzer.repository;

import com.example.AIResumeAnalyzer.model.ResumeAnalysis;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ResumeAnalysisRepository extends JpaRepository<ResumeAnalysis ,Long> {
    @Query("""
        SELECT ra
        FROM ResumeAnalysis ra
        JOIN ra.resume r
        JOIN r.user u
        WHERE u.id = :userId
    """)
    List<ResumeAnalysis> findByUserId(@Param("userId") Long userId);
}
