package com.code_space.code_space_editor.project_managment.service;

import java.time.Instant;
import java.util.List;

import org.springframework.stereotype.Service;

import com.code_space.code_space_editor.project_managment.entity.sql.FileVersion;
import com.code_space.code_space_editor.auth.utility.AuthUtils;
import com.code_space.code_space_editor.project_managment.entity.sql.Branch;
import com.code_space.code_space_editor.project_managment.entity.sql.File;
import com.code_space.code_space_editor.project_managment.repository.BranchRepository;
import com.code_space.code_space_editor.project_managment.repository.FileRepository;
import com.code_space.code_space_editor.exceptions.FileStorageException;
import com.code_space.code_space_editor.exceptions.ResourceNotFoundException;
import com.code_space.code_space_editor.project_managment.service.interfaces.FileServiceInterface;
import com.code_space.code_space_editor.project_managment.service.interfaces.FileVersionServiceInterface;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FileService implements FileServiceInterface {
    private final FileRepository fileRepository;
    private final BranchRepository branchRepository;
    private final FileVersionServiceInterface fileVersionService;
    private final AuthUtils authUtils;

    @Override
    public List<File> getByBranch(Long branchId) {
        return fileRepository.findByBranchId(branchId);
    }

    @Override
    @Transactional
    public File createFile(Long projectId, Long branchId, String fileName, String language, String extension) {
        Branch branch = getBranchOrThrow(branchId);
        validateFileUniqueness(branchId, fileName);

        File file = buildNewFile(branch, fileName, language, extension);
        File savedFile = fileRepository.save(file);

        fileVersionService.createNewVersion(projectId, branchId, savedFile, "", "Initial commit", 0L);
        return savedFile;
    }

    @Override
    @Transactional
    public File updateFileInfo(Long fileId, String name, String language, String extension) {
        File file = getFileOrThrow(fileId);
        updateFileProperties(file, name, language, extension);
        return fileRepository.save(file);
    }

    @Override
    @Transactional
    public FileVersion createFileVersion(Long fileId, String newContent, String message) {
        File file = getFileOrThrow(fileId);
        updateFileVersionInfo(file);
        fileRepository.save(file);

        return fileVersionService.createNewVersion(
                file.getBranch().getProject().getId(),
                file.getBranch().getId(),
                file,
                newContent,
                message,
                file.getVersionsCount());
    }

    @Override
    public List<File> getAllByBranchId(Long branchId) {
        Branch branch = getBranchOrThrow(branchId);
        return fileRepository.findByBranchId(branch.getId());
    }

    @Override
    public List<FileVersion> getAllFileVersions(Long fileId) {
        getFileOrThrow(fileId); // Validate file exists
        return fileVersionService.getFileVersions(fileId);
    }

    @Override
    @Transactional
    public List<File> forkFiles(Long branchId, Branch newBranch) {
        List<File> originalFiles = fileRepository.findByBranchId(branchId);
        Long userId = authUtils.getCurrentUserId();

        originalFiles.forEach(file -> forkSingleFile(file, newBranch, userId));
        return fileRepository.findByBranchId(newBranch.getId());
    }

    // Helper methods
    private Branch getBranchOrThrow(Long branchId) {
        return branchRepository.findById(branchId)
                .orElseThrow(() -> new ResourceNotFoundException("Branch not found"));
    }

    private File getFileOrThrow(Long fileId) {
        return fileRepository.findById(fileId)
                .orElseThrow(() -> new FileStorageException("File not found"));
    }

    private void validateFileUniqueness(Long branchId, String fileName) {
        if (fileRepository.existsByBranchIdAndName(branchId, fileName)) {
            throw new FileStorageException("File already exists in this branch with the same name");
        }
    }

    private File buildNewFile(Branch branch, String fileName, String language, String extension) {
        File file = new File();
        file.setBranch(branch);
        file.setCreatedBy(authUtils.getCurrentUserId());
        file.setName(fileName);
        file.setExtension(extension);
        file.setLanguage(language);
        file.setCreatedAt(Instant.now());
        file.setVersionsCount(0L);
        return file;
    }

    private void updateFileProperties(File file, String name, String language, String extension) {
        file.setName(name);
        file.setLanguage(language);
        file.setExtension(extension);
    }

    private void updateFileVersionInfo(File file) {
        file.setUpdatedAt(Instant.now());
        file.setVersionsCount(file.getVersionsCount() + 1);
    }

    private void forkSingleFile(File file, Branch newBranch, Long userId) {
        try {
            FileVersion latestVersion = fileVersionService.getLatestVersion(file.getId());
            String content = fileVersionService.readFileVersionContent(latestVersion);

            File newFile = createForkedFile(file, newBranch, userId);
            File savedFile = fileRepository.save(newFile);

            fileVersionService.createNewVersion(
                    newBranch.getProject().getId(),
                    newBranch.getId(),
                    savedFile,
                    content,
                    "Forked from another branch",
                    0L);
        } catch (Exception e) {
            // Log error and continue with next file
            // Consider adding proper logging
        }
    }

    private File createForkedFile(File original, Branch newBranch, Long userId) {
        File newFile = new File();
        newFile.setBranch(newBranch);
        newFile.setCreatedBy(userId);
        newFile.setName(original.getName());
        newFile.setExtension(original.getExtension());
        newFile.setLanguage(original.getLanguage());
        newFile.setCreatedAt(Instant.now());
        newFile.setVersionsCount(0L);
        return newFile;
    }

    @Override
    public String getFileContent(Long fileId) {
        getFileOrThrow(fileId);
        FileVersion latestVersion = fileVersionService.getLatestVersion(fileId);
        return fileVersionService.readFileVersionContent(latestVersion);
    }

    @Override
    public String getSpecificVersionContent(Long fileId, Long versionNumber) {
        getFileOrThrow(fileId);
        FileVersion specificVersion = fileVersionService.getVersionByFileIdAndVersionNumber(fileId, versionNumber);
        return fileVersionService.readFileVersionContent(specificVersion);
    }
}