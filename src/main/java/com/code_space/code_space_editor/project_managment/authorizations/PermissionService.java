package com.code_space.code_space_editor.project_managment.authorizations;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import com.code_space.code_space_editor.auth.utility.AuthUtils;
import com.code_space.code_space_editor.exceptions.ResourceNotFoundException;
import com.code_space.code_space_editor.project_managment.entity.sql.ProjectMember;
import com.code_space.code_space_editor.project_managment.repository.ProjectMemberRepository;
import com.code_space.code_space_editor.project_managment.repository.BranchRepository;
import com.code_space.code_space_editor.project_managment.entity.sql.Branch;
import com.code_space.code_space_editor.project_managment.entity.enums.ProjectRole;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Component("permissionService")
public class PermissionService {

    private final ProjectMemberRepository memberRepo;
    private final BranchRepository branchRepository;
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
     * 1. Check project permission level
     * 2. Verify branch exists within project
     */
    public boolean hasBranchPermission(Long projectId, Long branchId, String requiredRole) {
        if (!hasProjectPermission(projectId, requiredRole)) {
            throw new AccessDeniedException("You are not authorized to access this project.");
        }

        Branch branch = branchRepository.findById(branchId)
                .orElseThrow(() -> new ResourceNotFoundException("Branch not found with ID: " + branchId));

        if (!branch.getProject().getId().equals(projectId)) {
            throw new AccessDeniedException("Branch does not belong to the specified project.");
        }

        return true;
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
