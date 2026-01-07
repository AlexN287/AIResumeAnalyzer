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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ResumeServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private ResumeRepository resumeRepository;

    @Mock
    private OpenAIservice openAIservice;

    @Mock
    private ResumeAnalysisRepository resumeAnalysisRepository;

    @InjectMocks
    private ResumeServiceImpl resumeService;

    @Test
    void uploadResume_success() throws Exception {
        Long userId = 1L;
        User user = new User("john", "password");

        MultipartFile file = new MockMultipartFile(
                "file",
                "resume.pdf",
                "application/pdf",
                "dummy content".getBytes()
        );

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        when(resumeRepository.save(any(Resume.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        ResumeAnalysis fakeAnalysis = new ResumeAnalysis();
        fakeAnalysis.setStrengths("Good skills");
        fakeAnalysis.setWeaknesses("Needs improvement");
        fakeAnalysis.setSkillSuggestions("Java, Spring");
        fakeAnalysis.setOverallFeedback("Well-prepared candidate");

        when(openAIservice.analyzeResume(any(byte[].class), any(Resume.class)))
                .thenReturn(fakeAnalysis);

        when(resumeAnalysisRepository.save(any(ResumeAnalysis.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Resume resume = resumeService.uploadResume(userId, file);

        Assertions.assertNotNull(resume);
        Assertions.assertEquals("resume.pdf", resume.getFileName());
        Assertions.assertEquals(user, resume.getUser());
        Assertions.assertNotNull(resume.getUploadedFile());
        Assertions.assertEquals("application/pdf", resume.getUploadedFile().getContentType());

        Assertions.assertNotNull(resume.getResumeAnalysis());
        ResumeAnalysis analysis = resume.getResumeAnalysis();
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
        Long userId = 99L;
        MultipartFile file = new MockMultipartFile(
                "file",
                "resume.pdf",
                "application/pdf",
                "content".getBytes()
        );

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        Assertions.assertThrows(
                RuntimeException.class,
                () -> resumeService.uploadResume(userId, file)
        );
    }

    @Test
    void getResumeFileByUserId_shouldReturnUploadedFile() {
        Long userId = 1L;
        User user = new User();
        user.setId(userId);

        UploadedFile uploadedFile = new UploadedFile();
        uploadedFile.setFileName("resume.pdf");
        uploadedFile.setContentType("application/pdf");
        uploadedFile.setData("PDF content".getBytes());

        Resume resume = new Resume();
        resume.setUser(user);
        resume.setUploadedFile(uploadedFile);

        when(resumeRepository.findByUserId(userId)).thenReturn(Optional.of(resume));

        UploadedFile result = resumeService.getResumeFileByUserId(userId);

        Assertions.assertNotNull(result);
        Assertions.assertEquals("resume.pdf", result.getFileName());
        Assertions.assertArrayEquals("PDF content".getBytes(), result.getData());

        verify(resumeRepository).findByUserId(userId);
    }


    @Test
    void getResumeFileByUserId_shouldThrow_resumeNotFound() {
        Long userId = 1L;
        when(resumeRepository.findByUserId(userId)).thenReturn(Optional.empty());

        RuntimeException exception = Assertions.assertThrows(RuntimeException.class,
                () -> resumeService.getResumeFileByUserId(userId));

        Assertions.assertEquals("Resume not found", exception.getMessage());

        verify(resumeRepository).findByUserId(userId);
    }

}
