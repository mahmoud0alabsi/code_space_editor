package com.code_space.code_space_editor.project_managment.service;

import java.time.Instant;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.code_space.code_space_editor.project_managment.entity.enums.ProjectRole;
import com.code_space.code_space_editor.project_managment.dto.ProjectDTO;
import com.code_space.code_space_editor.project_managment.entity.sql.Project;
import com.code_space.code_space_editor.project_managment.entity.sql.ProjectMember;
import com.code_space.code_space_editor.project_managment.repository.ProjectMemberRepository;
import com.code_space.code_space_editor.project_managment.repository.ProjectRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProjectService {
    private final ProjectRepository projectRepository;
    private final ProjectMemberRepository projectMemberRepository;

    public Project create(Project project) {
        Project newProject = projectRepository.save(project);

        // Add the owner as a member of the project with the role "OWNER"
        ProjectMember ownerMember = new ProjectMember();
        ownerMember.setProject(newProject);
        ownerMember.setUserId(project.getOwnerId());
        ownerMember.setRole(ProjectRole.OWNER);
        projectMemberRepository.save(ownerMember);

        return newProject;
    }

    public Optional<Project> getById(Long id) {
        return projectRepository.findById(id);
    }

    public Project update(Long id, ProjectDTO dto) {
        Project project = projectRepository.findById(id).orElseThrow(() -> new RuntimeException("Project not found"));
        project.setName(dto.getName());
        project.setDescription(dto.getDescription());
        project.setUpdatedAt(Instant.now());
        return projectRepository.save(project);
    }

    public void delete(Long id) {
        Project project = projectRepository.findById(id).orElseThrow(() -> new RuntimeException("Project not found"));
        projectRepository.delete(project);
    }
}
