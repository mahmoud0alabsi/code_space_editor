package com.code_space.code_space_editor.project_managment.service;

import java.time.Instant;
import java.util.Collections;
import java.util.List;

import org.springframework.stereotype.Service;

import com.code_space.code_space_editor.auth.entity.User;
import com.code_space.code_space_editor.auth.utility.AuthUtils;
import com.code_space.code_space_editor.exceptions.ResourceNotFoundException;
import com.code_space.code_space_editor.project_managment.concurrency.ConcurrencyService;
import com.code_space.code_space_editor.project_managment.dto.commit.CreateCommitDTO;
import com.code_space.code_space_editor.project_managment.dto.file.CreateFileDTO;
import com.code_space.code_space_editor.project_managment.dto.file.FileDTO;
import com.code_space.code_space_editor.project_managment.entity.sql.Branch;
import com.code_space.code_space_editor.project_managment.entity.sql.Commit;
import com.code_space.code_space_editor.project_managment.repository.BranchRepository;
import com.code_space.code_space_editor.project_managment.repository.CommitRepository;
import com.code_space.code_space_editor.project_managment.service.utils.CommitServiceUtils;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CommitService {
    private final ConcurrencyService concurrencyService;
    private final CommitRepository commitRepository;
    private final BranchRepository branchRepository;
    private final CommitServiceUtils commitServiceUtils;
    private final FileService fileService;
    private final AuthUtils authUtils;

    @Transactional
    public Commit createCommit(Long branchId, CreateCommitDTO commitDTO) {
        concurrencyService.lockBranch(branchId);
        try {
            User user = authUtils.getCurrentUser();
            Branch branch = branchRepository.findById(branchId)
                    .orElseThrow(() -> new ResourceNotFoundException("Branch not found with ID: " + branchId));

            Commit parentCommit = getLatestCommit(branch.getId());
            Long parentCommitId = null;
            if (parentCommit != null) {
                parentCommitId = parentCommit.getId();
            }

            Commit commit = Commit.builder()
                    .author(user.getId())
                    .authorName(user.getUsername())
                    .branch(branch)
                    .message(commitDTO.getMessage())
                    .createdAt(Instant.now())
                    .parentCommit(parentCommitId)
                    .build();
            commit = commitRepository.save(commit);

            for (CreateFileDTO fileDTO : commitDTO.getFiles()) {
                fileService.createFile(
                        commit.getBranch().getProject().getId(),
                        commit.getBranch(),
                        commit,
                        fileDTO);
            }

            return commit;
        } catch (Exception e) {
            throw new RuntimeException("Error creating commit: " + e.getMessage(), e);
        } finally {
            concurrencyService.unlockBranch(branchId);
        }
    }

    public Commit getCommitById(Long commitId) {
        return commitRepository.findById(commitId)
                .orElseThrow(() -> new ResourceNotFoundException("Commit not found"));
    }

    public List<Commit> getAllCommits(Long branchId) {
        return commitRepository.findByBranchId(branchId);
    }

    public Commit getLatestCommit(Long branchId) {
        return commitServiceUtils.getLatestCommit(branchId);
    }

    @Transactional
    public List<FileDTO> getLatestCommitFiles(Long branchId, boolean includeContent) {
        Commit latestCommit = getLatestCommit(branchId);
        if (latestCommit == null) {
            return Collections.emptyList();
        }
        return getFilesByCommitId(latestCommit.getId(), includeContent);
    }

    @Transactional
    public List<FileDTO> getFilesByCommitId(Long commitId, boolean includeContent) {
        Commit commit = getCommitById(commitId);
        if (commit == null) {
            return Collections.emptyList();
        }
        return fileService.getFilesByCommitId(commitId, includeContent);
    }

    @Transactional
    public String getFileContentById(Long fileId) {
        return fileService.getFileContent(fileId);
    }
}
