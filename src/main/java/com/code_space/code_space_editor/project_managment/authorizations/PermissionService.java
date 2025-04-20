package com.code_space.code_space_editor.project_managment.authorizations;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import com.code_space.code_space_editor.auth.utility.AuthUtils;
import com.code_space.code_space_editor.project_managment.entity.sql.ProjectMember;
import com.code_space.code_space_editor.project_managment.repository.ProjectMemberRepository;
import com.code_space.code_space_editor.project_managment.repository.BranchRepository;
import com.code_space.code_space_editor.project_managment.repository.FileRepository;
import com.code_space.code_space_editor.project_managment.entity.sql.Branch;
import com.code_space.code_space_editor.project_managment.entity.enums.ProjectRole;
import com.code_space.code_space_editor.exceptions.ResourceNotFoundException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Component("permissionService")
public class PermissionService {

    private final ProjectMemberRepository memberRepo;
    private final BranchRepository branchRepository;
    private final FileRepository fileRepository;
    private final AuthUtils authUtils;

    /**
     * Check if user has permission over project based on role
     * OWNER: full access
     * COLLABORATOR: modify access
     * VIEWER: read-only access
     */
    public boolean hasProjectPermission(Long projectId, String requiredRole) {
        Long userId = authUtils.getCurrentUserId();
        ProjectMember member = getProjectMemberOrThrow(projectId, userId);
        String userRole = member.getRole().name();

        return checkRolePermission(userRole, requiredRole);
    }

    /**
     * Check if user has permission over branch
     * 1. Check project permission
     * 2. Verify branch exists within project
     */
    public boolean hasBranchPermission(Long projectId, Long branchId, String requiredRole) {
        // Check project-level permission first
        if (!hasProjectPermission(projectId, requiredRole)) {
            throw new AccessDeniedException("You are not authorized to access this project.");
        }

        // Verify branch exists within project
        Branch branch = branchRepository.findById(branchId)
                .orElseThrow(() -> new ResourceNotFoundException("Branch not found with ID: " + branchId));

        if (!branch.getProject().getId().equals(projectId)) {
            throw new AccessDeniedException("Branch does not belong to the specified project.");
        }

        return true;
    }

    /**
     * Check if user has permission over file using a single optimized query
     * Verifies project membership, branch ownership, and file ownership in one go
     */
    // TODO: This not implemented correctly
    public boolean hasFilePermission(Long projectId, Long branchId, Long fileId, String requiredRole) {
        Long userId = authUtils.getCurrentUserId();

        if (!hasProjectPermission(projectId, requiredRole)) {
            throw new AccessDeniedException("You are not authorized to access this project.");
        } else if (!hasBranchPermission(projectId, branchId, requiredRole)) {
            throw new AccessDeniedException("You are not authorized to access this branch.");
        }

        // Use optimized query for performance
        boolean hasPermission = hasFilePermissionDirect(projectId, branchId, fileId, userId, requiredRole);

        if (!hasPermission) {
            throw new AccessDeniedException("You are not authorized to access this file.");
        }

        return true;
    }

    private boolean hasFilePermissionDirect(Long projectId, Long branchId, Long fileId, Long userId,
            String requiredRole) {
        List<ProjectRole> allowedRoles;
        switch (requiredRole) {
            case "OWNER" -> allowedRoles = Collections.singletonList(ProjectRole.OWNER);
            case "COLLABORATOR" -> allowedRoles = Arrays.asList(ProjectRole.OWNER, ProjectRole.COLLABORATOR);
            case "VIEWER" ->
                allowedRoles = Arrays.asList(ProjectRole.OWNER, ProjectRole.COLLABORATOR, ProjectRole.VIEWER);
            default -> throw new IllegalArgumentException("Invalid required role: " + requiredRole);
        }

        return fileRepository.existsByProjectBranchFileAndUserWithRoles(
                projectId, branchId, fileId, userId, allowedRoles);
    }

    private ProjectMember getProjectMemberOrThrow(Long projectId, Long userId) {
        return memberRepo.findByProjectIdAndUserId(projectId, userId)
                .orElseThrow(() -> new AccessDeniedException(
                        "You are not a member of project with ID: " + projectId));
    }

    private boolean checkRolePermission(String userRole, String requiredRole) {
        switch (ProjectRole.valueOf(userRole)) {
            case OWNER -> {
                return true; // OWNER has access to all roles
            }
            case COLLABORATOR -> {
                return requiredRole.equals("COLLABORATOR") || requiredRole.equals("VIEWER");
            }
            case VIEWER -> {
                return requiredRole.equals("VIEWER");
            }
            default -> throw new AccessDeniedException("Invalid role: " + userRole);
        }
    }
}
