package com.example.AIResumeAnalyzer.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "uploaded_file")
@Getter
@Setter
@NoArgsConstructor
public class UploadedFile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String fileName;
    private String contentType;
    private LocalDateTime uploadedAt = LocalDateTime.now();

    @Column(name = "s3_key", nullable = false)
    private String s3Key;

    /** Not persisted — populated on demand after fetching bytes from S3. */
    @Transient
    private byte[] data;
}
