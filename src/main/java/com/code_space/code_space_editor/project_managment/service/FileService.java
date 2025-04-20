package com.code_space.code_space_editor.project_managment.service;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.code_space.code_space_editor.auth.utility.AuthUtils;
import com.code_space.code_space_editor.project_managment.entity.sql.Branch;
import com.code_space.code_space_editor.project_managment.entity.sql.File;
import com.code_space.code_space_editor.project_managment.repository.FileRepository;
import com.code_space.code_space_editor.exceptions.FileStorageException;
import com.code_space.code_space_editor.project_managment.concurrency.ConcurrencyService;
import com.code_space.code_space_editor.project_managment.dto.file.CreateFileDTO;
import com.code_space.code_space_editor.project_managment.dto.file.FileDTO;
import com.code_space.code_space_editor.project_managment.entity.sql.Commit;
import com.code_space.code_space_editor.project_managment.mapper.FileMapper;
import com.code_space.code_space_editor.project_managment.service.interfaces.FileServiceInterface;
import com.code_space.code_space_editor.project_managment.service.interfaces.FileStorageServiceInterface;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FileService implements FileServiceInterface {
    private final FileStorageServiceInterface fileStorageService;
    private final ConcurrencyService concurrencyService;
    private final FileRepository fileRepository;
    private final FileMapper fileMapper;
    private final AuthUtils authUtils;

    @Override
    @Transactional
    public File createFile(Long projectId, Branch branch, Commit commit, CreateFileDTO fileDTO) {
        File file = buildNewFile(branch, commit, fileDTO);
        File savedFile = fileRepository.save(file);

        concurrencyService.lockFileWrite(savedFile.getId());
        try {
            String filePath = fileStorageService.storeFile(projectId, branch.getId(), commit.getId(),
                    savedFile.getId(), fileDTO.getContent(), fileDTO.getExtension());

            savedFile.setPath(filePath);
            return fileRepository.save(savedFile);
        } finally {
            concurrencyService.unlockFileWrite(savedFile.getId());
        }
    }

    @Override
    @Transactional
    public File updateFileInfo(Long fileId, String name) {
        File file = getFileOrThrow(fileId);
        concurrencyService.lockFileWrite(fileId);
        try {
            file.setName(name);
            file.setUpdatedAt(Instant.now());
            return fileRepository.save(file);
        } finally {
            concurrencyService.unlockFileWrite(fileId);
        }
    }

    @Override
    @Transactional
    public void forkFiles(Long projectId, Long branhcId, Commit baseCommit, Commit newCommit) {
        List<File> originalFiles = fileRepository.findByCommitId(baseCommit.getId());
        originalFiles.forEach(file -> forkSingleFile(projectId, branhcId, file, newCommit));
    }

    @Override
    @Transactional
    public void deleteFilesByBranch(Branch branch) {
        List<File> files = fileRepository.findByBranchId(branch.getId());
        if (files.isEmpty()) {
            return;
        }

        for (File file : files) {
            try {
                fileStorageService.deleteFileByPath(file.getPath());
                fileRepository.delete(file);
            } catch (Exception e) {
                throw new FileStorageException("Error deleting file: " + e.getMessage(), e);
            }
        }
    }

    @Override
    public List<File> getByCommit(Long commitId) {
        return fileRepository.findByCommitId(commitId);
    }

    @Override
    public List<File> getAllByCommitId(Long commitId) {
        return fileRepository.findByCommitId(commitId);
    }

    @Transactional
    public List<FileDTO> getFilesByCommitId(Long commitId, boolean includeContent) {
        List<File> files = getByCommit(commitId);
        if (files.isEmpty()) {
            return Collections.emptyList();
        }

        return files.stream()
                .map(file -> {
                    FileDTO dto = fileMapper.toDTO(file);
                    if (includeContent) {
                        dto.setContent(fileStorageService.readFile(file.getPath()));
                    }
                    return dto;
                })
                .collect(Collectors.toList());
    }

    private File getFileOrThrow(Long fileId) {
        return fileRepository.findById(fileId)
                .orElseThrow(() -> new FileStorageException("File not found"));
    }

    private void validateFileUniqueness(Long commitId, String fileName) {
        if (fileRepository.existsByCommitIdAndName(commitId, fileName)) {
            throw new FileStorageException("File already exists in this branch with the same name");
        }
    }

    private File buildNewFile(Branch branch, Commit commit, CreateFileDTO fileDTO) {
        validateFileUniqueness(commit.getId(), fileDTO.getName());
        File file = new File();
        file.setBranch(branch);
        file.setCommit(commit);
        file.setAuthor(authUtils.getCurrentUserId());
        file.setName(fileDTO.getName());
        file.setExtension(fileDTO.getExtension());
        file.setLanguage(fileDTO.getLanguage());
        file.setPath(null);
        file.setCreatedAt(Instant.now());
        return file;
    }

    @Transactional
    private void forkSingleFile(Long projectId, Long branchId, File baseFile, Commit newCommit) {
        try {
            String content = fileStorageService.readFile(baseFile.getPath());
            CreateFileDTO fileDTO = CreateFileDTO.builder()
                    .name(baseFile.getName())
                    .extension(baseFile.getExtension())
                    .language(baseFile.getLanguage())
                    .content(content)
                    .build();

            File newFile = buildNewFile(
                    newCommit.getBranch(),
                    newCommit,
                    fileDTO);

            File savedFile = fileRepository.save(newFile);

            String filePath = fileStorageService.storeFile(projectId, branchId, newCommit.getId(),
                    savedFile.getId(),
                    fileDTO.getContent(), fileDTO.getExtension());

            savedFile.setPath(filePath);
            fileRepository.save(savedFile);
        } catch (Exception e) {
            throw new FileStorageException("Error forking file: " + e.getMessage(), e);
        }
    }

    @Override
    public String getFileContent(File file) {
        concurrencyService.lockFileRead(file.getId());
        try {
            return fileStorageService.readFile(file.getPath());
        } finally {
            concurrencyService.unlockFileRead(file.getId());
        }
    }

    @Override
    public String getFileContent(Long fileId) {
        return getFileContent(getFileOrThrow(fileId));
    }
}