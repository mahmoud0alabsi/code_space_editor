package com.code_space.code_space_editor.project_managment.service;

import java.time.Instant;
import java.util.List;

import org.springframework.stereotype.Service;

import com.code_space.code_space_editor.auth.utility.AuthUtils;
import com.code_space.code_space_editor.exceptions.ResourceNotFoundException;
import com.code_space.code_space_editor.project_managment.entity.sql.File;
import com.code_space.code_space_editor.project_managment.entity.sql.FileVersion;
import com.code_space.code_space_editor.project_managment.repository.FileVersionRepository;
import com.code_space.code_space_editor.project_managment.service.interfaces.FileStorageServiceInterface;
import com.code_space.code_space_editor.project_managment.service.interfaces.FileVersionServiceInterface;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FileVersionService implements FileVersionServiceInterface {
    private final FileVersionRepository fileVersionRepository;
    private final FileStorageServiceInterface fileStorageService;
    private final AuthUtils authUtils;

    @Override
    @Transactional
    public FileVersion createNewVersion(Long projectId, Long branchId, File file, String content,
            String message, Long versionNumber) {
        String filePath = fileStorageService.storeFile(projectId, branchId, file.getId(),
                versionNumber, content, file.getExtension());

        FileVersion fileVersion = buildFileVersion(file, filePath, message, versionNumber);
        return fileVersionRepository.save(fileVersion);
    }

    @Override
    public String readFileVersionContent(FileVersion fileVersion) {
        return fileStorageService.readFile(fileVersion.getPath());
    }

    @Override
    public List<FileVersion> getFileVersions(Long fileId) {
        return fileVersionRepository.findByFileId(fileId);
    }

    @Override
    public FileVersion getLatestVersion(Long fileId) {
        return fileVersionRepository.findTopByFileIdOrderByVersionNumberDesc(fileId)
                .orElseThrow(() -> new ResourceNotFoundException("No versions found for file ID: " + fileId));
    }

    // Helper methods
    private FileVersion buildFileVersion(File file, String filePath, String message, Long versionNumber) {
        FileVersion fileVersion = new FileVersion();
        fileVersion.setFile(file);
        fileVersion.setPath(filePath);
        fileVersion.setCreatedAt(Instant.now());
        fileVersion.setCreatedBy(authUtils.getCurrentUserId());
        fileVersion.setVersionNumber(versionNumber);
        fileVersion.setMessage(message);
        return fileVersion;
    }

    @Override
    public FileVersion getVersionByFileIdAndVersionNumber(Long fileId, Long versionNumber) {
        return fileVersionRepository.findByFileIdAndVersionNumber(fileId, versionNumber)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Version " + versionNumber + " not found for file ID: " + fileId));
    }
}
