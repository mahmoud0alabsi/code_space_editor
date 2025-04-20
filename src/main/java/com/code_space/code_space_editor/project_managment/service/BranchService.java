package com.code_space.code_space_editor.project_managment.service;

import java.time.Instant;
import java.util.List;

import org.springframework.stereotype.Service;

import com.code_space.code_space_editor.auth.entity.User;
import com.code_space.code_space_editor.auth.utility.AuthUtils;
import com.code_space.code_space_editor.exceptions.ResourceNotFoundException;
import com.code_space.code_space_editor.project_managment.dto.branch.CreateBranchDTO;
import com.code_space.code_space_editor.project_managment.entity.sql.Branch;
import com.code_space.code_space_editor.project_managment.entity.sql.Project;
import com.code_space.code_space_editor.project_managment.repository.BranchRepository;
import com.code_space.code_space_editor.project_managment.repository.ProjectRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BranchService {
    private final BranchRepository branchRepository;
    private final ProjectRepository projectRepository;
    private final AuthUtils authUtils;

    @Transactional
    public Branch create(Long projectId, CreateBranchDTO branchDTO) {
        User user = authUtils.getCurrentUser();

        Project project = projectRepository.findById(projectId)
                .orElseThrow(
                        () -> new ResourceNotFoundException("Project not found with ID: " + projectId));

        Branch branch = Branch.builder()
                .name(branchDTO.getName())
                .authorUsername(user.getUsername())
                .authorId(user.getId())
                .project(project)
                .baseBranchId(null)
                .createdAt(Instant.now())
                .updatedAt(null)
                .commits(null)
                .build();

        return branchRepository.save(branch);
    }

    @Transactional
    public void deleteBranch(Long projectId, Long branchId) {
        Branch branch = branchRepository.findByIdAndProjectId(branchId, projectId);
        if (branch == null) {
            throw new ResourceNotFoundException(
                    "Branch not found with ID: " + branchId + " in project ID: " + projectId);
        }

        branchRepository.deleteById(branchId);
    }

    public List<Branch> getAllByProjectId(Long projectId) {
        return branchRepository.findByProjectId(projectId);
    }

    public Branch getBranchById(Long branchId, Long projectId) {
        Branch branch = branchRepository.findByIdAndProjectId(branchId, projectId);
        if (branch == null) {
            throw new ResourceNotFoundException(
                    "Branch not found with ID: " + branchId + " in project ID: " + projectId);
        }

        return branch;
    }
}
