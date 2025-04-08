package com.code_space.code_space_editor.project_managment.service;

import java.time.Instant;
import java.util.List;

import org.springframework.stereotype.Service;

import com.code_space.code_space_editor.auth.entity.User;
import com.code_space.code_space_editor.exceptions.ResourceNotFoundException;
import com.code_space.code_space_editor.project_managment.dto.branch.CreateBranchDTO;
import com.code_space.code_space_editor.project_managment.dto.branch.ForkBranchDTO;
import com.code_space.code_space_editor.project_managment.entity.sql.Branch;
import com.code_space.code_space_editor.project_managment.entity.sql.File;
import com.code_space.code_space_editor.project_managment.entity.sql.Project;
import com.code_space.code_space_editor.project_managment.repository.BranchRepository;
import com.code_space.code_space_editor.project_managment.repository.ProjectRepository;
import com.code_space.code_space_editor.project_managment.repository.FileRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BranchService {
    private final BranchRepository branchRepository;
    private final ProjectRepository projectRepository;
    private final FileRepository fileRepository;
    private final FileService fileService;

    // Fetch all branches for a project
    public List<Branch> getAllByProjectId(Long projectId) {
        return branchRepository.findByProjectId(projectId);
    }

    // Fetch a branch by ID
    public Branch getBranchById(Long branchId, Long projectId) {
        // Check if the branch exists in the project
        Branch branch = branchRepository.findByIdAndProjectId(branchId, projectId);
        if (branch == null) {
            throw new ResourceNotFoundException(
                    "Branch not found with ID: " + branchId + " in project ID: " + projectId);
        }

        return branch;
    }

    // Create a new branch
    public Branch create(User user, CreateBranchDTO branchDTO, Long projectId) {
        // get the project by ID to ensure it exists
        Project project = projectRepository.findById(projectId)
                .orElseThrow(
                        () -> new ResourceNotFoundException("Project not found with ID: " + projectId));

        // Create a new branch entity
        Branch branch = new Branch();
        branch.setName(branchDTO.getName());
        branch.setCreatedByUsername(user.getUsername()); // Set the username of the user who created the branch
        branch.setCreatedById(user.getId()); // Set the user ID who created the branch
        branch.setProject(project); // Set the project for the new branch
        branch.setBaseBranchId(null);
        branch.setCreatedAt(Instant.now());
        branch.setUpdatedAt(null);
        branch.setFiles(null);

        return branchRepository.save(branch);
    }

    // Delete a branch by ID
    public void deleteBranch(Long projectId, Long branchId) {
        // Check if the branch exists in the project
        Branch branch = branchRepository.findByIdAndProjectId(branchId, projectId);
        if (branch == null) {
            throw new ResourceNotFoundException(
                    "Branch not found with ID: " + branchId + " in project ID: " + projectId);
        }

        branchRepository.deleteById(branchId);
    }

    // Get files associated with a branch
    public List<File> getAllFilesInBranch(Long branchId) {
        return fileRepository.findByBranchId(branchId);
    }

    // Fork a branch (create a new branch with the same base branch)
    // TODO: Implement the logic forkBranch method
    public Branch forkBranch(User user, Long branchId, Long projectId, ForkBranchDTO branch) {
        Branch baseBranch = getBranchById(branchId, projectId); // Fetch the base branch
        return baseBranch;
        // if (baseBranch == null) {
        // throw new ResourceNotFoundException("Base branch not found with ID: " +
        // branchId);
        // }

        // // Check if the project exists
        // Project project = projectRepository.findById(projectId)
        // .orElseThrow(() -> new ResourceNotFoundException("Project not found with ID:
        // " + projectId));

        // // Create a new branch entity for the forked branch
        // Branch branchToFork = new Branch();
        // branchToFork.setName(branch.getName());
        // branchToFork.setCreatedByUsername(user.getUsername()); // Set the username of
        // the user who created the branch
        // branchToFork.setCreatedById(user.getId()); // Set the user ID who created the
        // branch
        // branchToFork.setProject(project); // Set the project for the new branch
        // branchToFork.setBaseBranchId(branchId); // Set base branch
        // branchToFork.setCreatedAt(Instant.now());
        // branchToFork.setUpdatedAt(null);
        // branchToFork.setFiles(fileService.forkFiles(user.getId(), branchId,
        // branchToFork)); // Copy files from the base
        // // branch

        // return branchRepository.save(branchToFork);
    }

}
