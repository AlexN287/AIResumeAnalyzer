package com.example.AIResumeAnalyzer.service.implementations;

import com.example.AIResumeAnalyzer.Utils.PDFUtils;
import com.example.AIResumeAnalyzer.model.Resume;
import com.example.AIResumeAnalyzer.model.ResumeAnalysis;
import com.example.AIResumeAnalyzer.model.UploadedFile;
import com.example.AIResumeAnalyzer.model.User;
import com.example.AIResumeAnalyzer.openAI.OpenAIservice;
import com.example.AIResumeAnalyzer.repository.ResumeAnalysisRepository;
import com.example.AIResumeAnalyzer.repository.ResumeRepository;
import com.example.AIResumeAnalyzer.repository.UserRepository;
import com.example.AIResumeAnalyzer.service.ResumeService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class ResumeServiceImpl implements ResumeService {
    private final ResumeRepository resumeRepository;
    private final UserRepository userRepository;
    private final OpenAIservice openAIservice;
    private final ResumeAnalysisRepository resumeAnalysisRepository;

    @Transactional
    @Override
    public Resume uploadResume(Long userId, MultipartFile file) throws IOException {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        UploadedFile uploadedFile = new UploadedFile();
        uploadedFile.setFileName(file.getOriginalFilename());
        uploadedFile.setContentType(file.getContentType());
        uploadedFile.setData(file.getBytes());

        Resume resume = new Resume(
                user,
                file.getOriginalFilename(),
                uploadedFile,
                LocalDate.now()
        );

        resume = resumeRepository.save(resume);

        // 5️⃣ Call OpenAI
        ResumeAnalysis analysis = openAIservice.analyzeResume(file.getBytes(), resume);

        resumeAnalysisRepository.save(analysis);

        // 7️⃣ Link analysis to resume
        resume.setResumeAnalysis(analysis);

        return resume;
    }

    @Transactional
    @Override
    public UploadedFile getResumeFileByUserId(Long userId) {

        Resume resume = resumeRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Resume not found"));

        return resume.getUploadedFile();
    }
}
