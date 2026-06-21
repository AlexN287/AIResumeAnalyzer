package com.example.AIResumeAnalyzer.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.mcp.SyncMcpToolCallbackProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Optional;

@Configuration
public class ChatClientConfig {

    private static final Logger log = LoggerFactory.getLogger(ChatClientConfig.class);

    @Bean
    ChatClient chatClient(ChatModel chatModel,
                          @Autowired(required = false) SyncMcpToolCallbackProvider mcpTools) {

        ChatClient.Builder builder = ChatClient.builder(chatModel);

        if (mcpTools != null) {
            for (int i = 0; i < mcpTools.getToolCallbacks().length; i++) {
                log.debug("----------------------------------");
                log.debug("MCP Tool Index: {}", i);
                log.debug("name {}", mcpTools.getToolCallbacks()[i].getToolDefinition().name());
                log.debug("Description {}", mcpTools.getToolCallbacks()[i].getToolDefinition().description());
                log.debug("Schema {}", mcpTools.getToolCallbacks()[i].getToolDefinition().inputSchema());
                log.debug("----------------------------------");
            }
            builder.defaultToolCallbacks(mcpTools.getToolCallbacks());
            log.info("MCP tools registered: {} tool(s)", mcpTools.getToolCallbacks().length);
        } else {
            log.info("MCP client is disabled — ChatClient will run without MCP tools");
        }

        return builder.build();
    }
}