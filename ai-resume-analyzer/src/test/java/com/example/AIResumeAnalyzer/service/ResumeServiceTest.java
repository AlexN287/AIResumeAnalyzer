package com.example.AIResumeAnalyzer.service;

import com.example.AIResumeAnalyzer.model.Resume;
import com.example.AIResumeAnalyzer.model.ResumeAnalysis;
import com.example.AIResumeAnalyzer.model.UploadedFile;
import com.example.AIResumeAnalyzer.model.User;
import com.example.AIResumeAnalyzer.openAI.OpenAIservice;
import com.example.AIResumeAnalyzer.repository.ResumeAnalysisRepository;
import com.example.AIResumeAnalyzer.repository.ResumeRepository;
import com.example.AIResumeAnalyzer.repository.UserRepository;
import com.example.AIResumeAnalyzer.service.implementations.ResumeServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ResumeServiceTest {

    @Mock private UserRepository userRepository;
    @Mock private ResumeRepository resumeRepository;
    @Mock private OpenAIservice openAIservice;
    @Mock private ResumeAnalysisRepository resumeAnalysisRepository;

    @Mock
    private OpenAIservice openAIservice;

    @Mock
    private ResumeAnalysisRepository resumeAnalysisRepository;

    @InjectMocks
    private ResumeServiceImpl resumeService;

    private Long userId;
    private Long resumeId;

    private User user;
    private MultipartFile multipartFile;

    private UploadedFile uploadedFile;
    private Resume resume;

    private ResumeAnalysis fakeAnalysis;

    @BeforeEach
    void setUp() throws Exception {
        userId = 1L;
        resumeId = 1L;

        user = new User("john", "password");
        user.setId(userId);

        multipartFile = new MockMultipartFile(
                "file",
                "resume.pdf",
                "application/pdf",
                "dummy content".getBytes()
        );

        uploadedFile = new UploadedFile();
        uploadedFile.setId(10L);
        uploadedFile.setFileName("cv.pdf");
        uploadedFile.setContentType("application/pdf");
        uploadedFile.setData(new byte[]{1, 2, 3});

        resume = new Resume();
        resume.setId(resumeId);
        resume.setFileName("cv.pdf");
        resume.setUser(user);
        resume.setUploadedFile(uploadedFile);

        fakeAnalysis = new ResumeAnalysis();
        fakeAnalysis.setStrengths("Good skills");
        fakeAnalysis.setWeaknesses("Needs improvement");
        fakeAnalysis.setSkillSuggestions("Java, Spring");
        fakeAnalysis.setOverallFeedback("Well-prepared candidate");
    }

    @Test
    void uploadResume_success() throws Exception {
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        when(resumeRepository.save(any(Resume.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        when(openAIservice.analyzeResume(any(byte[].class), any(Resume.class)))
                .thenReturn(fakeAnalysis);

        when(resumeAnalysisRepository.save(any(ResumeAnalysis.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Resume saved = resumeService.uploadResume(userId, multipartFile);

        Assertions.assertNotNull(saved);
        Assertions.assertEquals("resume.pdf", saved.getFileName());
        Assertions.assertEquals(user, saved.getUser());

        Assertions.assertNotNull(saved.getUploadedFile());
        Assertions.assertEquals("application/pdf", saved.getUploadedFile().getContentType());

        Assertions.assertNotNull(saved.getResumeAnalysis());
        ResumeAnalysis analysis = saved.getResumeAnalysis();
        Assertions.assertEquals("Good skills", analysis.getStrengths());
        Assertions.assertEquals("Needs improvement", analysis.getWeaknesses());
        Assertions.assertEquals("Java, Spring", analysis.getSkillSuggestions());
        Assertions.assertEquals("Well-prepared candidate", analysis.getOverallFeedback());

        verify(userRepository).findById(userId);
        verify(resumeRepository).save(any(Resume.class));
        verify(openAIservice).analyzeResume(any(byte[].class), any(Resume.class));
        verify(resumeAnalysisRepository).save(any(ResumeAnalysis.class));
    }

    @Test
    void uploadResume_userNotFound() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        RuntimeException ex = Assertions.assertThrows(
                RuntimeException.class,
                () -> resumeService.uploadResume(99L, multipartFile)
        );

        Assertions.assertEquals("User not found", ex.getMessage());

        verify(userRepository).findById(99L);
        verifyNoInteractions(resumeRepository, openAIservice, resumeAnalysisRepository);
    }

    @Test
    void getResumeFileByResumeId_success() {
        when(resumeRepository.findById(resumeId)).thenReturn(Optional.of(resume));

        UploadedFile result = resumeService.getResumeFileByResumeId(resumeId);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(10L, result.getId());
        Assertions.assertEquals("cv.pdf", result.getFileName());
        Assertions.assertEquals("application/pdf", result.getContentType());
        Assertions.assertArrayEquals(new byte[]{1, 2, 3}, result.getData());

        verify(resumeRepository).findById(resumeId);
        verifyNoMoreInteractions(resumeRepository);
    }

    @Test
    void getResumeFileByResumeId_notFound_throws() {
        when(resumeRepository.findById(99L)).thenReturn(Optional.empty());

        RuntimeException ex = Assertions.assertThrows(RuntimeException.class,
                () -> resumeService.getResumeFileByResumeId(99L));

        Assertions.assertEquals("Resume not found", ex.getMessage());

        verify(resumeRepository).findById(99L);
        verifyNoMoreInteractions(resumeRepository);
    }

    @Test
    void getResumeFileByUserId_shouldReturnUploadedFile() {
        when(resumeRepository.findByUserId(userId)).thenReturn(Optional.of(resume));

        UploadedFile result = resumeService.getResumeFileByUserId(userId);

        Assertions.assertNotNull(result);
        Assertions.assertEquals("cv.pdf", result.getFileName());
        Assertions.assertArrayEquals(new byte[]{1, 2, 3}, result.getData());

        verify(resumeRepository).findByUserId(userId);
    }

    @Test
    void getResumeFileByUserId_shouldThrow_resumeNotFound() {
        when(resumeRepository.findByUserId(userId)).thenReturn(Optional.empty());

        RuntimeException exception = Assertions.assertThrows(RuntimeException.class,
                () -> resumeService.getResumeFileByUserId(userId));

        Assertions.assertEquals("Resume not found", exception.getMessage());

        verify(resumeRepository).findByUserId(userId);
    }
}