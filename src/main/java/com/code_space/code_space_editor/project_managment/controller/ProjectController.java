package com.code_space.code_space_editor.project_managment.controller;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.time.Instant;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.code_space.code_space_editor.auth.utility.AuthUtils;
import com.code_space.code_space_editor.exceptions.ResourceNotFoundException;
import com.code_space.code_space_editor.project_managment.dto.ProjectDTO;
import com.code_space.code_space_editor.project_managment.entity.sql.Project;
import com.code_space.code_space_editor.project_managment.mapper.ProjectMapper;
import com.code_space.code_space_editor.project_managment.service.ProjectService;

import lombok.RequiredArgsConstructor;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;

@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/api/v1/projects")
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectService projectService;
    private final ProjectMapper projectMapper;
    private final AuthUtils authUtils;

    @PostMapping
    public ResponseEntity<String> createProject(@Valid @RequestBody ProjectDTO dto) {
        dto.setOwnerId(authUtils.getCurrentUserId());
        dto.setCreatedAt(Instant.now());
        dto.setUpdatedAt(null);
        Project project = projectService.create(projectMapper.toEntity(dto));
        return ResponseEntity.ok("Project created with ID: " + project.getId());
    }

    @GetMapping("/{id}")
    @PreAuthorize("@permissionService.hasProjectPermission(#id, 'VIEWER')")
    public ResponseEntity<ProjectDTO> getProject(@PathVariable Long id) {
        Project project = projectService.getById(id).orElse(null);
        if (project == null) {
            throw new ResourceNotFoundException("Project not found with ID: " + id);
        }

        ProjectDTO projectDTO = projectMapper.toDTO(project);
        return ResponseEntity.ok(projectDTO);
    }

    // TODO: Test -> /api/projects/{PUT}
    @PutMapping("/{id}")
    @PreAuthorize("@permissionService.hasProjectPermission(#id, 'OWNER')")
    public ResponseEntity<ProjectDTO> updateProject(@PathVariable Long id, @RequestBody ProjectDTO dto) {
        Project updated = projectService.update(id, dto);
        return ResponseEntity.ok(projectMapper.toDTO(updated));
    }

    // TODO: Test -> /api/projects/{DELETE}
    @DeleteMapping("/{id}")
    @PreAuthorize("@permissionService.hasProjectPermission(#id, 'OWNER')")
    public ResponseEntity<Void> deleteProject(@PathVariable Long id) {
        projectService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
