package com.example.AIResumeAnalyzer.repository;

import com.example.AIResumeAnalyzer.model.Resume;
import com.example.AIResumeAnalyzer.model.UploadedFile;
import com.example.AIResumeAnalyzer.model.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDate;
import java.util.Optional;

@DataJpaTest
public class ResumeRepositoryTest {
    @Autowired
    private ResumeRepository resumeRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    void shouldFindResumeByUserId() {
        User user = new User();
        user = userRepository.save(user);

        UploadedFile file = new UploadedFile();
        file.setFileName("resume.pdf");
        file.setContentType("application/pdf");
        file.setData("pdf".getBytes());

        Resume resume = new Resume(
                user,
                "resume.pdf",
                file,
                LocalDate.now()
        );

        resumeRepository.save(resume);

        Optional<Resume> result = resumeRepository.findByUserId(user.getId());

        Assertions.assertTrue(result.isPresent());
        Assertions.assertEquals(user.getId(), result.get().getUser().getId());
        Assertions.assertEquals("resume.pdf", result.get().getFileName());
    }

    @Test
    void shouldReturnEmptyWhenUserHasNoResume() {
        Optional<Resume> result = resumeRepository.findByUserId(999L);

        Assertions.assertTrue(result.isEmpty());
    }
}
