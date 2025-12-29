package com.example.AIResumeAnalyzer.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "resume")
@Getter @Setter
@NoArgsConstructor
public class Resume {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private String fileName;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "uploaded_file_id", nullable = false)
    private UploadedFile uploadedFile;

    private LocalDate uploadDate;

    @OneToOne(mappedBy = "resume", cascade = CascadeType.ALL, orphanRemoval = true)
    private ResumeAnalysis resumeAnalysis;

    public Resume(User user, String fileName, UploadedFile uploadedFile, LocalDate uploadDate) {
        this.user = user;
        this.fileName = fileName;
        this.uploadedFile = uploadedFile;
        this.uploadDate = uploadDate;
    }
}
