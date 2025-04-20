package com.code_space.code_space_editor.project_managment.service;

import java.time.Instant;

import com.code_space.code_space_editor.exceptions.ResourceNotFoundException;
import com.code_space.code_space_editor.project_managment.entity.sql.Branch;
import com.code_space.code_space_editor.project_managment.entity.sql.Commit;
import com.code_space.code_space_editor.project_managment.repository.BranchRepository;
import com.code_space.code_space_editor.project_managment.repository.CommitRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import com.code_space.code_space_editor.auth.entity.User;
import com.code_space.code_space_editor.auth.utility.AuthUtils;
import com.code_space.code_space_editor.project_managment.service.utils.CommitServiceUtils;

@Service
@RequiredArgsConstructor
public class ForkBranchService {
    private final CommitRepository commitRepository;
    private final BranchRepository branchRepository;
    private final CommitServiceUtils commitServiceUtils;
    private final FileService fileService;
    private final AuthUtils authUtils;

    @Transactional
    public Branch forkBranch(Long projectId, Long baseBranchId, String newBranchName) {
        User user = authUtils.getCurrentUser();

        Branch baseBranch = branchRepository.findById(baseBranchId)
                .orElseThrow(() -> new ResourceNotFoundException("Base branch not found with ID: " + baseBranchId));

        // Check if branch name is unique within the project
        if (branchRepository.existsByNameAndProjectId(newBranchName, baseBranch.getProject().getId())) {
            throw new IllegalArgumentException("Branch name '" + newBranchName + "' already exists in this project");
        }

        Branch newBranch = Branch.builder()
                .name(newBranchName)
                .authorId(user.getId())
                .authorUsername(user.getUsername())
                .project(baseBranch.getProject())
                .baseBranchId(baseBranchId)
                .createdAt(Instant.now())
                .updatedAt(null)
                .build();
        newBranch = branchRepository.save(newBranch);

        Commit baseCommit = commitServiceUtils.getLatestCommit(baseBranchId);
        if (baseCommit != null) {
            // Create a new commit for the forked branch
            Commit newCommit = Commit.builder()
                    .author(user.getId())
                    .branch(newBranch)
                    .message("Forked from branch " + baseBranch.getName() + " at commit " + baseCommit.getId())
                    .createdAt(Instant.now())
                    .parentCommit(baseCommit.getId()) // Link to base commit
                    .build();
            newCommit = commitRepository.save(newCommit);

            // Fork files from the base commit to the new commit
            fileService.forkFiles(projectId, newBranch.getId(), baseCommit, newCommit);
        }

        return newBranch;
    }
}
