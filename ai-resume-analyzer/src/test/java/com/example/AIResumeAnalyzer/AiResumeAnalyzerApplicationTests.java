package com.example.AIResumeAnalyzer;

import com.example.AIResumeAnalyzer.openAI.OpenAIservice;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import software.amazon.awssdk.services.s3.S3Client;

@SpringBootTest
@ActiveProfiles("test")
class AiResumeAnalyzerApplicationTests {

    @MockitoBean
    private OpenAIservice openAIservice;

    @MockitoBean
    private S3Client s3Client;

	@Test
	void contextLoads() {
	}

}
