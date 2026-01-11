package com.example.AIResumeAnalyzer.openAI;

import com.example.AIResumeAnalyzer.Utils.PDFUtils;
import com.example.AIResumeAnalyzer.model.Resume;
import com.example.AIResumeAnalyzer.model.ResumeAnalysis;

import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class OpenAIservice {

    private final ChatModel chatModel;

    public OpenAIservice(ChatModel chatModel) {
        this.chatModel = chatModel;
    }

    public ResumeAnalysis analyzeResume(byte[] resumeData, Resume resume) {
        String resumeText = PDFUtils.extractText(resumeData);

        if (resumeText == null || resumeText.trim().isEmpty()) {
            throw new IllegalArgumentException("Resume text extraction failed");
        }

        String aiText = callOpenAI(resumeText);

        return parseAIResponse(aiText, resume);
    }

    private String callOpenAI(String resumeText) {
        String system = """
            You are an assistant that analyzes resumes.
            Follow the user's output format exactly.
            """;

        String userTemplate = """
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
            {resume}
            """;

        PromptTemplate template = new PromptTemplate(userTemplate);
        String userText = template.render(Map.of("resume", resumeText));

        OpenAiChatOptions options = OpenAiChatOptions.builder()
                .temperature(0.2)
                .build();

        Prompt prompt = new Prompt(
                java.util.List.of(
                        new SystemMessage(system),
                        new UserMessage(userText)
                ),
                options
        );

        ChatResponse response = chatModel.call(prompt);

        String content = response.getResult().getOutput().getText();
        return (content != null) ? content : "";
    }

    private ResumeAnalysis parseAIResponse(String aiText, Resume resume) {
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