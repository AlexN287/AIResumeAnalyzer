package com.example.AIResumeAnalyzer.repository;

import com.example.AIResumeAnalyzer.model.Resume;
import com.example.AIResumeAnalyzer.model.ResumeAnalysis;
import com.example.AIResumeAnalyzer.model.UploadedFile;
import com.example.AIResumeAnalyzer.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.util.List;

@DataJpaTest
public class ResumeAnalysisTest {
    @Autowired
    private ResumeAnalysisRepository resumeAnalysisRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    void findByUserId_returnsEmpty_whenUserHasNoAnalyses() {
        User user = new User("noAnalyses", "pass");
        user = userRepository.save(user);

        List<ResumeAnalysis> result = resumeAnalysisRepository.findByUserId(user.getId());

        assertThat(result).isEmpty();
    }
}

