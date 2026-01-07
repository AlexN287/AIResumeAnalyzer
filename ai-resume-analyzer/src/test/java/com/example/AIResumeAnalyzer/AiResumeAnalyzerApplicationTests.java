package com.example.AIResumeAnalyzer;

import com.example.AIResumeAnalyzer.openAI.OpenAIservice;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@SpringBootTest
@ActiveProfiles("test")
class AiResumeAnalyzerApplicationTests {

    @MockitoBean
    private OpenAIservice openAIservice;

	@Test
	void contextLoads() {
	}

}
