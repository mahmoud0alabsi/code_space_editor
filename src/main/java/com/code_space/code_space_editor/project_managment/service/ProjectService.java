package com.code_space.code_space_editor.project_managment.service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.code_space.code_space_editor.auth.utility.AuthUtils;
import com.code_space.code_space_editor.project_managment.entity.enums.ProjectRole;
import com.code_space.code_space_editor.project_managment.dto.project.CreateProjectDTO;
import com.code_space.code_space_editor.project_managment.entity.sql.Project;
import com.code_space.code_space_editor.project_managment.entity.sql.ProjectMember;
import com.code_space.code_space_editor.project_managment.repository.ProjectMemberRepository;
import com.code_space.code_space_editor.project_managment.repository.ProjectRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProjectService {
    private final ProjectRepository projectRepository;
    private final ProjectMemberRepository projectMemberRepository;
    private final AuthUtils authUtils;

    @Transactional
    public Project create(CreateProjectDTO projectDto) {
        Project project = Project.builder()
                .ownerId(authUtils.getCurrentUserId())
                .name(projectDto.getName())
                .description(projectDto.getDescription())
                .createdAt(Instant.now())
                .updatedAt(null)
                .branches(List.of())
                .members(List.of())
                .build();

        Project newProject = projectRepository.save(project);

        System.out.println("=============================");
        System.out.println("Project created: " + newProject.getName());
        System.out.println("Project Id: " + newProject.getId());
        System.out.println("=============================");

        // Add the owner as a member of the project with the role "OWNER"
        ProjectMember ownerMember = ProjectMember.builder()
                .project(newProject)
                .userId(newProject.getOwnerId())
                .role(ProjectRole.OWNER)
                .build();
        projectMemberRepository.save(ownerMember);

        return newProject;
    }

    @Transactional
    public Project update(Long id, CreateProjectDTO dto) {
        Project project = projectRepository.findById(id).orElseThrow(() -> new RuntimeException("Project not found"));
        project.setName(dto.getName());
        project.setDescription(dto.getDescription());
        project.setUpdatedAt(Instant.now());
        return projectRepository.save(project);
    }

    @Transactional
    public void delete(Long id) {
        Project project = projectRepository.findById(id).orElseThrow(() -> new RuntimeException("Project not found"));
        projectRepository.delete(project);
    }

    public Optional<Project> getById(Long id) {
        return projectRepository.findById(id);
    }

    public List<Project> getUserProjects() {
        // get the current user id from the security context
        Long userId = authUtils.getCurrentUserId();
        // get the projects where the user is a member
        return projectMemberRepository.findByUserId(userId).stream()
                .map(ProjectMember::getProject)
                .toList();
    }
}
