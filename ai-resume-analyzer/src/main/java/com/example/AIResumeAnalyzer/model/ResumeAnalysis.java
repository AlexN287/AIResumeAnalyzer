package com.example.AIResumeAnalyzer.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "resume_analysis")
@Getter @Setter
@NoArgsConstructor
public class ResumeAnalysis {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "resume_id", nullable = false)
    private Resume resume;

    @Column(columnDefinition = "TEXT")
    private String strengths;

    @Column(columnDefinition = "TEXT")
    private String weaknesses;

    @Column(name = "skill_suggestions", columnDefinition = "TEXT")
    private String skillSuggestions;

    @Column(columnDefinition = "TEXT")
    private String overallFeedback;

    public ResumeAnalysis(Resume resume, String strengths, String weaknesses, String skillSuggestions, String overallFeedback) {
        this.resume = resume;
        this.strengths = strengths;
        this.weaknesses = weaknesses;
        this.skillSuggestions = skillSuggestions;
        this.overallFeedback = overallFeedback;
    }
}
