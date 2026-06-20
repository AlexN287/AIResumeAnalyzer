package com.example.AIResumeAnalyzer.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

/**
 * Configures the AWS S3 client.
 *
 * Credentials are resolved via the Default Credentials Provider Chain:
 *  - Locally:   ~/.aws/credentials  (supports aws_session_token for student accounts)
 *  - On AWS:    IAM role attached to the EC2 / ECS / Lambda instance
 *
 * No secrets are stored in application.yml or in code.
 */
@Configuration
public class S3Config {

    @Value("${aws.s3.region}")
    private String region;

    @Bean
    public S3Client s3Client() {
        return S3Client.builder()
                .region(Region.of(region))
                .credentialsProvider(DefaultCredentialsProvider.create())
                .build();
    }
}

