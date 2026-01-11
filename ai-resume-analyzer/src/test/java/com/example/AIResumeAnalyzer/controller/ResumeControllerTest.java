package com.example.AIResumeAnalyzer.controller;

import com.example.AIResumeAnalyzer.model.Resume;
import com.example.AIResumeAnalyzer.model.UploadedFile;
import com.example.AIResumeAnalyzer.service.ResumeService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@ExtendWith(MockitoExtension.class)
class ResumeControllerTest {

    private MockMvc mockMvc;

    @Mock
    private ResumeService resumeService;

    @InjectMocks
    private ResumeController resumeController;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(resumeController).build();
    }

    @Test
    void uploadResume_success() throws Exception {
        Resume resume = new Resume();
        resume.setId(1L);
        resume.setFileName("resume.pdf");
        resume.setUploadDate(LocalDate.now());

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "resume.pdf",
                "application/pdf",
                "dummy content".getBytes()
        );

        when(resumeService.uploadResume(eq(1L), any(MultipartFile.class)))
                .thenReturn(resume);

        mockMvc.perform(
                        multipart("/api/resumes/upload")
                                .file(file)
                                .param("userId", "1")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resumeId").value(1))
                .andExpect(jsonPath("$.fileName").value("resume.pdf"));
    }

    @Test
    void uploadResume_emptyFile() throws Exception {
        MockMultipartFile emptyFile = new MockMultipartFile(
                "file",
                "",
                "application/pdf",
                new byte[0]
        );

        mockMvc.perform(
                        multipart("/api/resumes/upload")
                                .file(emptyFile)
                                .param("userId", "1")
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    void getResumePdf_shouldReturnPdf() throws Exception {
        Long resumeId = 1L;

        UploadedFile file = new UploadedFile();
        file.setFileName("resume.pdf");
        file.setContentType("application/pdf");
        file.setData("PDF content".getBytes());

        when(resumeService.getResumeFileByResumeId(resumeId)).thenReturn(file);

        mockMvc.perform(get("/api/resumes/{resumeId}/pdf", resumeId))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"resume.pdf\""))
                .andExpect(content().contentType(MediaType.APPLICATION_PDF))
                .andExpect(content().bytes("PDF content".getBytes()));

        verify(resumeService).getResumeFileByResumeId(resumeId);
    }

    @Test
    void getResumePdf_shouldReturnNotFound_whenResumeMissing() throws Exception {
        Long resumeId = 1L;

        when(resumeService.getResumeFileByResumeId(resumeId))
                .thenThrow(new RuntimeException("Resume not found"));

        mockMvc.perform(get("/api/resumes/{resumeId}/pdf", resumeId))
                .andExpect(status().isNotFound());

        verify(resumeService).getResumeFileByResumeId(resumeId);
    }


}
