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

import com.code_space.code_space_editor.project_managment.dto.branch.BranchDTO;
import com.code_space.code_space_editor.project_managment.dto.branch.CreateBranchDTO;
import com.code_space.code_space_editor.project_managment.dto.branch.ForkBranchDTO;
import com.code_space.code_space_editor.project_managment.entity.sql.Branch;
import com.code_space.code_space_editor.project_managment.mapper.BranchMapper;
import com.code_space.code_space_editor.project_managment.service.ForkBranchService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/branches")
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Branch Management", description = "Branch management operations")
public class BranchController {

    private final BranchService branchService;
    private final ForkBranchService forkBranchService;
    private final BranchMapper branchMapper;

    @PostMapping("/p/{projectId}")
    @PreAuthorize("@permissionService.hasProjectPermission(#projectId, 'COLLABORATOR')")
    @Operation(summary = "Create a new branch")
    public Branch createBranch(
            @PathVariable Long projectId,
            @Valid @RequestBody CreateBranchDTO branchDTO) {
        return branchService.create(
                projectId,
                branchDTO);
    }

    @DeleteMapping("/p/{projectId}/b/{branchId}")
    @PreAuthorize("@permissionService.hasBranchPermission(#projectId, #branchId, 'COLLABORATOR')")
    @Operation(summary = "Delete a branch")
    public ResponseEntity<String> deleteBranch(
            @PathVariable Long projectId,
            @PathVariable Long branchId) {
        branchService.deleteBranch(projectId, branchId);
        return ResponseEntity.ok("Branch deleted successfully");
    }

    @PostMapping("/p/{projectId}/b/{branchId}/fork")
    @PreAuthorize("@permissionService.hasBranchPermission(#projectId, #branchId, 'COLLABORATOR')")
    @Operation(summary = "Fork a branch")
    public Branch forkBranch(
            @PathVariable Long projectId,
            @PathVariable Long branchId,
            @Valid @RequestBody ForkBranchDTO branch) {
        return forkBranchService.forkBranch(
                projectId, branchId, branch.getName());
    }

    @GetMapping("/p/{projectId}")
    @PreAuthorize("@permissionService.hasProjectPermission(#projectId, 'VIEWER')")
    @Operation(summary = "Get all branches by project ID")
    public List<BranchDTO> getBranchesByProjectId(@PathVariable Long projectId) {
        return branchService.getAllByProjectId(projectId)
                .stream()
                .map(branchMapper::toDTO)
                .toList(); // Convert to DTOs if needed
    }

    @GetMapping("/p/{projectId}/b/{branchId}")
    @PreAuthorize("@permissionService.hasBranchPermission(#projectId, #branchId, 'VIEWER')")
    @Operation(summary = "Get a branch by ID")
    public BranchDTO getBranchById(
            @PathVariable Long projectId,
            @PathVariable Long branchId) {
        return branchMapper.toDTO(branchService.getBranchById(branchId, projectId));
    }
}
