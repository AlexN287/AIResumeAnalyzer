package com.example.AIResumeAnalyzer.service;

import com.example.AIResumeAnalyzer.model.Resume;
import com.example.AIResumeAnalyzer.model.ResumeAnalysis;
import com.example.AIResumeAnalyzer.model.UploadedFile;
import com.example.AIResumeAnalyzer.repository.ResumeAnalysisRepository;
import com.example.AIResumeAnalyzer.repository.ResumeRepository;
import com.example.AIResumeAnalyzer.repository.UploadedFileRepository;
import com.example.AIResumeAnalyzer.service.implementations.ResumeAnalysisImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ResumeAnalysisTest {
    @Mock
    private ResumeAnalysisRepository resumeAnalysisRepository;

    @Mock
    private ResumeRepository resumeRepository;

    @Mock
    private UploadedFileRepository uploadedFileRepository;

    @Mock
    private S3StorageService s3StorageService;

    @InjectMocks
    private ResumeAnalysisImpl resumeAnalysisService;

    private ResumeAnalysis analysis;
    private Resume resume;
    private UploadedFile uploadedFile;

    @BeforeEach
    void setUp() {
        uploadedFile = new UploadedFile();
        uploadedFile.setId(1L);
        uploadedFile.setFileName("test.pdf");
        uploadedFile.setS3Key("resumes/1/uuid_test.pdf");

        resume = new Resume();
        resume.setId(1L);
        resume.setFileName("resume.pdf");
        resume.setUploadedFile(uploadedFile);

        analysis = new ResumeAnalysis();
        analysis.setId(1L);
        analysis.setResume(resume);

        resume.setResumeAnalysis(analysis);
    }

    @Test
    void deleteResumeAnalysis_success() {
        when(resumeAnalysisRepository.findById(1L))
                .thenReturn(Optional.of(analysis));

        resumeAnalysisService.deleteResumeAnalysis(1L);

        verify(resumeAnalysisRepository).delete(analysis);
        verify(resumeRepository).delete(resume);
        verify(uploadedFileRepository).delete(uploadedFile);
        verify(s3StorageService).deleteFile("resumes/1/uuid_test.pdf");
    }

    @Test
    void deleteResumeAnalysis_notFound() {
        when(resumeAnalysisRepository.findById(1L))
                .thenReturn(Optional.empty());

        RuntimeException exception = Assertions.assertThrows(RuntimeException.class, () -> {
            resumeAnalysisService.deleteResumeAnalysis(1L);
        });

        Assertions.assertEquals("ResumeAnalysis not found", exception.getMessage());

        verify(resumeAnalysisRepository, Mockito.never()).delete(Mockito.any());
        verify(resumeRepository, Mockito.never()).delete(Mockito.any());
        verify(uploadedFileRepository, Mockito.never()).delete(Mockito.any());
        verify(s3StorageService, Mockito.never()).deleteFile(Mockito.any());
    }
}
