// package com.code_space.code_space_editor.project_managment.controller;

// import jakarta.validation.Valid;
// import lombok.RequiredArgsConstructor;

// import org.springframework.http.ResponseEntity;
// import org.springframework.security.access.prepost.PreAuthorize;

// import java.util.List;

// import org.springframework.web.bind.annotation.RequestBody;
// import org.springframework.web.bind.annotation.RequestMapping;
// import org.springframework.web.bind.annotation.RestController;
// import org.springframework.web.bind.annotation.PathVariable;
// import org.springframework.web.bind.annotation.GetMapping;
// import org.springframework.web.bind.annotation.PostMapping;

// import com.code_space.code_space_editor.project_managment.entity.sql.File;
// import
// com.code_space.code_space_editor.project_managment.entity.sql.FileVersion;
// import
// com.code_space.code_space_editor.project_managment.service.FileService;

// import org.springframework.http.MediaType;
// import org.springframework.web.bind.annotation.PutMapping;

// import
// com.code_space.code_space_editor.project_managment.dto.file.CreateFileDTO;
// import
// com.code_space.code_space_editor.project_managment.dto.file.CreateFileVersionDTO;

// import io.swagger.v3.oas.annotations.security.SecurityRequirement;

// @SecurityRequirement(name = "bearerAuth")
// @RestController
// @RequestMapping("/api/v1/projects/{projectId}/branches/{branchId}")
// @RequiredArgsConstructor
// public class FileController {

// private final FileService fileService;

// // Create a new file (save first version)
// @PostMapping("/files")
// @PreAuthorize("@permissionService.hasBranchPermission(#projectId, #branchId,
// 'COLLABORATOR')")
// public ResponseEntity<File> createFile(
// @PathVariable Long projectId,
// @PathVariable Long branchId,
// @Valid @RequestBody CreateFileDTO fileDTO) {
// File file = fileService.createFile(projectId, branchId, fileDTO.getName(),
// fileDTO.getLanguage(),
// fileDTO.getExtension());
// return ResponseEntity.ok(file);
// }

// // Update an existing file (create a new version)
// @PostMapping("/files/{fileId}")
// @PreAuthorize("@permissionService.hasFilePermission(#projectId, #branchId,
// #fileId, 'COLLABORATOR')")
// public ResponseEntity<FileVersion> creatFileVersion(
// @PathVariable Long projectId,
// @PathVariable Long branchId,
// @PathVariable Long fileId,
// @RequestBody CreateFileVersionDTO newVersion) {
// FileVersion fileVersion = fileService.createFileVersion(fileId,
// newVersion.getContent(),
// newVersion.getMessage());
// return ResponseEntity.ok(fileVersion);
// }

// // Update an existing file (create a new version)
// @PutMapping("/files/{fileId}")
// @PreAuthorize("@permissionService.hasFilePermission(#projectId, #branchId,
// #fileId, 'COLLABORATOR')")
// public ResponseEntity<File> updateFileInfo(
// @PathVariable Long projectId,
// @PathVariable Long branchId,
// @PathVariable Long fileId,
// @Valid @RequestBody CreateFileDTO fileDTO) {
// File file = fileService.updateFileInfo(fileId, fileDTO.getName(),
// fileDTO.getLanguage(), fileDTO.getExtension());
// return ResponseEntity.ok(file);
// }

// // Get all files for a branch
// @GetMapping("/files")
// @PreAuthorize("@permissionService.hasBranchPermission(#projectId, #branchId,
// 'VIEWER')")
// public ResponseEntity<List<File>> getFilesByBranchId(
// @PathVariable Long projectId,
// @PathVariable Long branchId) {
// List<File> files = fileService.getAllByBranchId(branchId);
// return ResponseEntity.ok(files);
// }

// @GetMapping("/files/{fileId}")
// @PreAuthorize("@permissionService.hasFilePermission(#projectId, #branchId,
// #fileId, 'VIEWER')")
// public ResponseEntity<String> getFileContent(
// @PathVariable Long projectId,
// @PathVariable Long branchId,
// @PathVariable Long fileId) {
// validatePathVariables(projectId, branchId, fileId);
// String content = fileService.getFileContent(fileId);
// return ResponseEntity.ok()
// .contentType(MediaType.TEXT_PLAIN)
// .body(content);
// }

// // Get all versions of a file
// @GetMapping("/files/{fileId}/versions")
// @PreAuthorize("@permissionService.hasFilePermission(#projectId, #branchId,
// #fileId, 'VIEWER')")
// public ResponseEntity<List<FileVersion>> getFileVersions(
// @PathVariable Long projectId,
// @PathVariable Long branchId,
// @PathVariable Long fileId) {
// List<FileVersion> versions = fileService.getAllFileVersions(fileId);
// return ResponseEntity.ok(versions);
// }

// @GetMapping("/files/{fileId}/versions/{versionNumber}")
// @PreAuthorize("@permissionService.hasFilePermission(#projectId, #branchId,
// #fileId, 'VIEWER')")
// public ResponseEntity<String> getSpecificFileVersionContent(
// @PathVariable Long projectId,
// @PathVariable Long branchId,
// @PathVariable Long fileId,
// @PathVariable Long versionNumber) {
// validatePathVariables(projectId, branchId, fileId);
// if (versionNumber < 0) {
// throw new IllegalArgumentException("Version number cannot be negative");
// }
// String content = fileService.getSpecificVersionContent(fileId,
// versionNumber);
// return ResponseEntity.ok()
// .contentType(MediaType.TEXT_PLAIN)
// .body(content);
// }

// private void validatePathVariables(Long projectId, Long branchId, Long
// fileId) {
// if (projectId == null || branchId == null || fileId == null) {
// throw new IllegalArgumentException("Path variables cannot be null");
// }
// }
// }
