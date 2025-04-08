package com.code_space.code_space_editor.project_managment.service.interfaces;

public interface FileStorageServiceInterface {
    String storeFile(Long projectId, Long branchId, Long fileId, Long versionNumber, String content, String extension);

    String readFile(String filePath);

    void deleteFile(Long projectId, Long branchId, Long fileId);
}