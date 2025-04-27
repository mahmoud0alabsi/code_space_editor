package com.code_space.code_space_editor.project_managment.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.code_space.code_space_editor.exceptions.FileStorageException;
import com.code_space.code_space_editor.project_managment.concurrency.ConcurrencyService;
import com.code_space.code_space_editor.project_managment.service.interfaces.FileStorageServiceInterface;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FileStorageService implements FileStorageServiceInterface {
    @Value("${file.storage.root-dir:/uploads}")
    private String ROOT_DIR;

    private final ConcurrencyService concurrencyService;

    @Override
    public String storeFile(Long projectId, Long branchId, Long commitId, Long fileId,
            String content, String extension) {
        Path filePath = buildFilePath(projectId, branchId, commitId, fileId, extension);
        String normalizedPath = normalizePath(filePath.toString());

        concurrencyService.lockFilePathWrite(normalizedPath);
        try {
            Files.createDirectories(filePath.getParent());
            Files.write(filePath, content.getBytes());
            return normalizedPath;
        } catch (IOException e) {
            throw new FileStorageException("Could not store file " + fileId, e);
        } finally {
            concurrencyService.unlockFilePathWrite(normalizedPath);
        }
    }

    @Override
    public String readFile(String filePath) {
        concurrencyService.lockFilePathRead(filePath);
        try {
            return new String(Files.readAllBytes(Paths.get(filePath)));
        } catch (IOException e) {
            throw new FileStorageException("Could not read file", e);
        } finally {
            concurrencyService.unlockFilePathRead(filePath);
        }
    }

    @Override
    public void deleteFile(Long projectId, Long branchId, Long commitId, Long fileId, String extension) {
        Path filePath = buildFilePath(projectId, branchId, commitId, fileId, extension);
        String normalizedPath = normalizePath(filePath.toString());
        concurrencyService.lockFilePathWrite(normalizedPath);
        try {
            Files.deleteIfExists(filePath);
        } catch (IOException e) {
            throw new FileStorageException("Failed to delete file " + fileId, e);
        } finally {
            concurrencyService.unlockFilePathWrite(normalizedPath);
        }
    }

    @Override
    public void deleteFileByPath(String filePath) {
        concurrencyService.lockFilePathWrite(filePath);
        try {
            Files.deleteIfExists(Paths.get(filePath));
        } catch (IOException e) {
            throw new FileStorageException("Failed to delete file at " + filePath, e);
        } finally {
            concurrencyService.unlockFilePathWrite(filePath);
        }
    }

    private Path buildFilePath(Long projectId, Long branchId, Long commitId, Long fileId, String extension) {
        // projectId/branchId/commitId/fileId.extension
        return Paths.get(ROOT_DIR,
                projectId.toString(),
                branchId.toString(),
                commitId.toString(),
                fileId.toString() + extension);
    }

    private String normalizePath(String filePath) {
        return filePath.replace("\\", "/");
    }
}
