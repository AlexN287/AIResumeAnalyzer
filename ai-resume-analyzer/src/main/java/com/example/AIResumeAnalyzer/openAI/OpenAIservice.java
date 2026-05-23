package com.example.AIResumeAnalyzer.openAI;

import com.example.AIResumeAnalyzer.Utils.PDFUtils;
import com.example.AIResumeAnalyzer.model.Resume;
import com.example.AIResumeAnalyzer.model.ResumeAnalysis;

import org.springframework.ai.chat.client.ChatClient;
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

    private final ChatClient chatClient;

    public OpenAIservice(ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    public ResumeAnalysis analyzeResume(byte[] resumeData, Resume resume) {
        String resumeText = PDFUtils.extractText(resumeData);

        if (resumeText == null || resumeText.trim().isEmpty()) {
            throw new IllegalArgumentException("Resume text extraction failed");
        }

        String aiText = callAI(resumeText);

        return parseAIResponse(aiText, resume);
    }

    private String callAI(String resumeText) {
        String system = """
            You are an AI resume analyzer with access to Playwright tools.
            If a DuckDuckGo search tool is available, you may use it to enrich GitHub context and job discovery.
            
            Your goal is to produce a structured resume analysis using reliable evidence only.
            
            Link handling rules:
            
            1. YouTube links
            - You MUST always use the provided YouTube transcript as part of your analysis.
            - If a transcript is not provided, proceed using only the resume.
            - Do NOT use Playwright for YouTube at this time.
            
            2. GitHub links
            - If a GitHub profile or repository link is provided, you MUST use Playwright tools to open it.
            - You MAY also use DuckDuckGo (if available) to find additional public context about notable projects.
            - If the link is a profile:
              - Navigate to the repositories section.
              - Identify up to 3–5 relevant or recently active public repositories.
            - For selected repositories:
              - Understand what the project does in simple terms.
              - Identify primary technologies and skills demonstrated.
              - Observe activity and maturity signals when visible (updates, stars, documentation quality).
              - Note any visible strengths or gaps (clarity, testing, structure, maintenance).
            
            3. Job recommendations
            - Use DuckDuckGo and/or Playwright (if available) to find relevant job opportunities based on the candidate’s skills.
            - Prefer reputable sources (company career pages, job boards, ATS platforms).
            - Recommend 3–6 realistic job roles with a short reason why each matches the profile.
            - Place job recommendations inside the "Overall Feedback" section.
            
            General rules:
            - Use tools only when they improve accuracy or provide real external evidence.
            - Never mention tools, browsing, automation, or scraping in the final answer.
            - Do not fabricate information if a link cannot be accessed or verified.
            - Follow the output format exactly:
            
            Strengths:
            - ...
            
            Weaknesses:
            - ...
            
            Skill Suggestions:
            - ...
            
            Overall Feedback:
            ...
            """;

        String userTemplate = """
            Analyze the following resume.
            
            If a GitHub link is present, access it with PlayWright and include insights from the candidate’s public projects and highlight at least one important or representative repository.
            If a youtube link is present, you can add some details about the videos in the analysis.
            
            In the "Overall Feedback" section, give an overall feedback about resume and include 2 or 3 recommended job opportunities the candidate could realistically apply for, you can use DuckDuckGo and Playwright to search for jobs.
            
            Return the result strictly in the following format:
            
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

        String content = chatClient
                .prompt()
                .system(system)
                .user(userText)
                .options(options)
                .call()
                .content();

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
