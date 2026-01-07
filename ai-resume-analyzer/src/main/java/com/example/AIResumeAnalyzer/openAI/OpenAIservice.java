package com.example.AIResumeAnalyzer.openAI;

import com.example.AIResumeAnalyzer.Utils.PDFUtils;
import com.example.AIResumeAnalyzer.model.Resume;
import com.example.AIResumeAnalyzer.model.ResumeAnalysis;
import com.openai.client.OpenAIClient;
import com.openai.client.okhttp.OpenAIOkHttpClient;
import com.openai.models.responses.Response;
import com.openai.models.responses.ResponseCreateParams;
import org.springframework.stereotype.Service;

@Service
public class OpenAIservice {

    private final OpenAIClient openAIClient;

    public OpenAIservice() {
        this.openAIClient = OpenAIOkHttpClient.fromEnv();
    }

    /*public String chatWithAI(String userMessage) {
        ResponseCreateParams params = ResponseCreateParams.builder()
                .model("gpt-5-nano")
                .input(userMessage)
                .build();

        Response response = openAIClient.responses().create(params);

        return response.output().get(1).message().get().content().get(0).outputText().get().text();
    }*/

    public ResumeAnalysis analyzeResume(byte[] resumeData, Resume resume) {
        // 1. Extract text from PDF
        String resumeText = PDFUtils.extractText(resumeData);

        if (resumeText == null || resumeText.trim().isEmpty()) {
            throw new IllegalArgumentException("Resume text extraction failed");
        }

        // 2. Build prompt
        String prompt = """
            Analyze the following resume.
            Return the result in the following format:

            Strengths:
            - ...

            Weaknesses:
            - ...

            Skill Suggestions:
            - ...

            Overall Feedback:
            ...

            Resume:
            %s
            """.formatted(resumeText);

        // 3. Call OpenAI
        String aiText = callOpenAI(prompt);

        // 4. Parse response into ResumeAnalysis
        return parseAIResponse(aiText, resume);
    }

    private String callOpenAI(String prompt) {
        ResponseCreateParams params = ResponseCreateParams.builder()
                .model("gpt-5-nano")
                .input(prompt)
                .build();

        Response response = openAIClient.responses().create(params);

        // Get the text output from the first ResponseOutputItem
        if (response.output() != null && !response.output().isEmpty()) {
            return response.output().get(1).message().get().content().get(0).outputText().get().text();
        }

        return "";
    }

    private ResumeAnalysis parseAIResponse(String aiText, Resume resume) {
        // Extract each section
        String strengths = extractSection(aiText, "Strengths:", "Weaknesses:");
        String weaknesses = extractSection(aiText, "Weaknesses:", "Skill Suggestions:");
        String skillSuggestions = extractSection(aiText, "Skill Suggestions:", "Overall Feedback:");
        String overallFeedback = extractSection(aiText, "Overall Feedback:", null);

        return new ResumeAnalysis(resume, strengths, weaknesses, skillSuggestions, overallFeedback);
    }

    private String extractSection(String text, String startLabel, String endLabel) {
        int start = text.indexOf(startLabel);
        if (start == -1) return "";

        start += startLabel.length();
        int end = (endLabel != null) ? text.indexOf(endLabel, start) : text.length();
        if (end == -1) end = text.length();

        return text.substring(start, end).trim();
    }
}