package com.code_space.code_space_editor.project_managment.controller;

import com.code_space.code_space_editor.project_managment.service.BranchService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.DeleteMapping;

import com.code_space.code_space_editor.auth.utility.AuthUtils;
import com.code_space.code_space_editor.project_managment.dto.branch.CreateBranchDTO;
import com.code_space.code_space_editor.project_managment.dto.branch.ForkBranchDTO;
import com.code_space.code_space_editor.project_managment.entity.sql.Branch;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;

@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/api/v1/projects/{projectId}/branches")
@RequiredArgsConstructor
public class BranchController {

    private final BranchService branchService;
    private final AuthUtils authUtils;

    // Get all branches for a specific project
    @GetMapping
    @PreAuthorize("@permissionService.hasProjectPermission(#projectId, 'VIEWER')")
    public List<Branch> getBranchesByProject(@PathVariable Long projectId) {
        return branchService.getAllByProjectId(projectId);
    }

    // Get a single branch by ID
    @GetMapping("/{branchId}")
    @PreAuthorize("@permissionService.hasBranchPermission(#projectId, #branchId, 'VIEWER')")
    public Branch getBranchById(
            @PathVariable Long projectId,
            @PathVariable Long branchId) {
        return branchService.getBranchById(branchId, projectId); // Service method to fetch a branch by ID
    }

    // Create a new branch in a project
    @PostMapping
    @PreAuthorize("@permissionService.hasProjectPermission(#projectId, 'COLLABORATOR')")
    public Branch createBranch(
            @PathVariable Long projectId,
            @Valid @RequestBody CreateBranchDTO branchDTO) {
        return branchService.create(
                authUtils.getCurrentUser(),
                branchDTO, projectId);
    }

    // Delete a branch by ID
    @DeleteMapping("/{branchId}")
    @PreAuthorize("@permissionService.hasBranchPermission(#projectId, #branchId, 'COLLABORATOR')")
    public ResponseEntity<String> deleteBranch(
            @PathVariable Long projectId,
            @PathVariable Long branchId) {
        branchService.deleteBranch(projectId, branchId);
        return ResponseEntity.ok("Branch deleted successfully");
    }

    // Fork a branch
    @PostMapping("/{branchId}/fork")
    @PreAuthorize("@permissionService.hasBranchPermission(#projectId, #branchId, 'COLLABORATOR')")
    public Branch forkBranch(
            @PathVariable Long projectId,
            @PathVariable Long branchId,
            @Valid @RequestBody ForkBranchDTO branch) {
        return branchService.forkBranch(
                authUtils.getCurrentUser(),
                branchId, projectId, branch);
    }
}
