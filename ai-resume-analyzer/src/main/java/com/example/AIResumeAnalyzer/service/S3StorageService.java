package com.example.AIResumeAnalyzer.service;

public interface S3StorageService {

    /**
     * Uploads bytes to S3 under the given key.
     *
     * @param key         object key (e.g. "resumes/1/uuid_resume.pdf")
     * @param data        raw file bytes
     * @param contentType MIME type (e.g. "application/pdf")
     */
    void uploadFile(String key, byte[] data, String contentType);

    /**
     * Downloads the object identified by {@code key} from S3 and returns its bytes.
     *
     * @param key object key
     * @return raw file bytes
     */
    byte[] downloadFile(String key);

    /**
     * Deletes the object identified by {@code key} from S3.
     *
     * @param key object key
     */
    void deleteFile(String key);
}

