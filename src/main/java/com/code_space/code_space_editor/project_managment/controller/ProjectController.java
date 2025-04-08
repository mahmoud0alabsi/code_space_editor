package com.code_space.code_space_editor.project_managment.controller;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.code_space.code_space_editor.exceptions.ResourceNotFoundException;
import com.code_space.code_space_editor.project_managment.dto.ProjectDTO;
import com.code_space.code_space_editor.project_managment.dto.project.CreateProjectDTO;
import com.code_space.code_space_editor.project_managment.entity.sql.Project;
import com.code_space.code_space_editor.project_managment.mapper.ProjectMapper;
import com.code_space.code_space_editor.project_managment.service.ProjectService;

import lombok.RequiredArgsConstructor;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/projects")
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Project Management", description = "Project management operations")
public class ProjectController {

    private final ProjectService projectService;
    private final ProjectMapper projectMapper;

    @PostMapping
    @Operation(summary = "Create a new project")
    public ResponseEntity<String> createProject(@Valid @RequestBody CreateProjectDTO dto) {
        Project project = projectService.create(dto);
        return ResponseEntity.ok("Project created with ID: " + project.getId());
    }

    @PutMapping("/{id}")
    @PreAuthorize("@permissionService.hasProjectPermission(#id, 'OWNER')")
    public ResponseEntity<ProjectDTO> updateProject(@PathVariable Long id, @Valid @RequestBody CreateProjectDTO dto) {
        Project updated = projectService.update(id, dto);
        return ResponseEntity.ok(projectMapper.toDTO(updated));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("@permissionService.hasProjectPermission(#id, 'OWNER')")
    public ResponseEntity<String> deleteProject(@PathVariable Long id) {
        projectService.delete(id);
        return ResponseEntity.ok("Project deleted successfully");
    }

    @GetMapping
    @Operation(summary = "Get all projects")
    public ResponseEntity<List<ProjectDTO>> getAllProjects() {
        List<Project> projects = projectService.getUserProjects();
        List<ProjectDTO> projectDTOs = projects.stream()
                .map(projectMapper::toDTO)
                .toList();

        return ResponseEntity.ok(projectDTOs);
    }

    @GetMapping("/{id}")
    @PreAuthorize("@permissionService.hasProjectPermission(#id, 'VIEWER')")
    @Operation(summary = "Get project by ID")
    public ResponseEntity<ProjectDTO> getProjectById(@PathVariable Long id) {
        Project project = projectService.getById(id).orElse(null);
        if (project == null) {
            throw new ResourceNotFoundException("Project not found with ID: " + id);
        }

        ProjectDTO projectDTO = projectMapper.toDTO(project);
        return ResponseEntity.ok(projectDTO);
    }

}
