package com.code_space.code_space_editor.project_managment.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import com.code_space.code_space_editor.project_managment.dto.commit.CommitDTO;
import com.code_space.code_space_editor.project_managment.dto.commit.CreateCommitDTO;
import com.code_space.code_space_editor.project_managment.dto.file.FileDTO;
import com.code_space.code_space_editor.project_managment.entity.sql.Commit;
import com.code_space.code_space_editor.project_managment.mapper.CommitMapper;
import com.code_space.code_space_editor.project_managment.service.CommitService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/commits")
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Commit Management", description = "Commit management operations")
public class CommitController {
    private final CommitService commitService;
    private final CommitMapper commitMapper;

    @PostMapping("/p/{projectId}/b/{branchId}")
    @PreAuthorize("@permissionService.hasBranchPermission(#projectId, #branchId, 'COLLABORATOR')")
    @Operation(summary = "Create a new commit")
    public ResponseEntity<Commit> createCommit(
            @PathVariable Long projectId,
            @PathVariable Long branchId,
            @Valid @RequestBody CreateCommitDTO commitDTO) {
        Commit commit = commitService.createCommit(branchId, commitDTO);
        return ResponseEntity.ok(commit);
    }

    @GetMapping("/p/{projectId}/b/{branchId}")
    @PreAuthorize("@permissionService.hasBranchPermission(#projectId, #branchId, 'VIEWER')")
    @Operation(summary = "Get all commits by branch ID")
    public ResponseEntity<List<CommitDTO>> getAllCommitsByBranchId(
            @PathVariable Long projectId,
            @PathVariable Long branchId) {
        List<Commit> commits = commitService.getAllCommits(branchId);
        return ResponseEntity.ok(
                commits.stream()
                        .map(commitMapper::toDTO)
                        .collect(Collectors.toList()));
    }

    @GetMapping("/p/{projectId}/b/{branchId}/c/{commitId}")
    @PreAuthorize("@permissionService.hasBranchPermission(#projectId, #branchId, 'VIEWER')")
    @Operation(summary = "Get commit by ID")
    public ResponseEntity<CommitDTO> getCommitById(
            @PathVariable Long projectId,
            @PathVariable Long branchId,
            @PathVariable Long commitId) {
        Commit commit = commitService.getCommitById(commitId);
        return ResponseEntity.ok(commitMapper.toDTO(commit));
    }

    @GetMapping("/p/{projectId}/b/{branchId}/latest")
    @PreAuthorize("@permissionService.hasBranchPermission(#projectId, #branchId, 'VIEWER')")
    @Operation(summary = "Get latest commit by branch ID")
    public ResponseEntity<CommitDTO> getLatestCommitByBranchId(
            @PathVariable Long projectId,
            @PathVariable Long branchId) {
        Commit commit = commitService.getLatestCommit(branchId);
        return ResponseEntity.ok(commitMapper.toDTO(commit));
    }

    @GetMapping("/p/{projectId}/b/{branchId}/latest/files")
    @PreAuthorize("@permissionService.hasBranchPermission(#projectId, #branchId, 'VIEWER')")
    @Operation(summary = "Get files of the latest commit by branch ID")
    public ResponseEntity<List<FileDTO>> getLatestCommitFilesByBranchId(
            @PathVariable Long projectId,
            @PathVariable Long branchId,
            @RequestParam(defaultValue = "false") boolean includeContent) {

        List<FileDTO> fileDTOs = commitService.getLatestCommitFiles(branchId, includeContent);
        if (fileDTOs.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(fileDTOs);
    }

    @CrossOrigin(origins = "http://localhost:3000")
    @GetMapping("/p/{projectId}/b/{branchId}/latest/files/{fileId}")
    @PreAuthorize("@permissionService.hasBranchPermission(#projectId, #branchId, 'VIEWER')")
    @Operation(summary = "Get file content by file ID in the latest commit")
    public ResponseEntity<String> getFileContentById(
            @PathVariable Long projectId,
            @PathVariable Long branchId,
            @PathVariable Long fileId) {
        String fileContent = commitService.getFileContentById(fileId);
        if (fileContent.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(fileContent);
    }

    @GetMapping("/p/{projectId}/b/{branchId}/c/{commitId}/files")
    @PreAuthorize("@permissionService.hasBranchPermission(#projectId, #branchId, 'VIEWER')")
    @Operation(summary = "Get files of a commit by commit ID")
    public ResponseEntity<List<FileDTO>> getCommitFilesByCommitId(
            @PathVariable Long projectId,
            @PathVariable Long branchId,
            @PathVariable Long commitId,
            @RequestParam(defaultValue = "false") boolean includeContent) {

        List<FileDTO> fileDTOs = commitService.getFilesByCommitId(commitId, includeContent);
        if (fileDTOs.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(fileDTOs);
    }
}