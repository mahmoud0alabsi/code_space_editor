package com.code_space.code_space_editor.project_managment.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.code_space.code_space_editor.auth.utility.AuthUtils;
import com.code_space.code_space_editor.project_managment.entity.enums.ProjectRole;
import com.code_space.code_space_editor.project_managment.entity.sql.Project;
import com.code_space.code_space_editor.project_managment.entity.sql.ProjectMember;
import com.code_space.code_space_editor.project_managment.repository.ProjectMemberRepository;

import com.code_space.code_space_editor.exceptions.ResourceNotFoundException;
import com.code_space.code_space_editor.project_managment.repository.ProjectRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProjectMemberService {
    private final ProjectMemberRepository projectMemberRepository;
    private final ProjectRepository projectRepository;
    private final AuthUtils authUtils;

    @Transactional
    public ProjectMember add(Long projectId, Long userId, ProjectRole role) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found"));

        if (projectMemberRepository.existsByProjectIdAndUserId(projectId, userId)) {
            throw new ResourceNotFoundException("User is already a member of the project");
        }

        ProjectMember member = ProjectMember.builder()
                .project(project)
                .userId(userId)
                .role(role)
                .build();
        return projectMemberRepository.save(member);
    }

    @Transactional
    public void removeMemberFromProject(Long projectId, Long memberId) {
        ProjectMember member = projectMemberRepository.findByProjectIdAndUserId(projectId, memberId)
                .orElseThrow(() -> new ResourceNotFoundException("Member not found"));

        projectMemberRepository.delete(member);
    }

    @Transactional
    public ProjectMember changeRole(Long projectId, Long userId, ProjectRole role) {
        ProjectMember currentMember = projectMemberRepository
                .findByProjectIdAndUserId(projectId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Member not found"));

        currentMember.setRole(role);
        return projectMemberRepository.save(currentMember);
    }

    public List<ProjectMember> getAllMembers(Long projectId) {
        return projectMemberRepository.findAllByProjectId(projectId);
    }

    public ProjectMember getProjectMemberOrThrow(Long projectId) {
        Long userId = authUtils.getCurrentUserId();
        return projectMemberRepository.findByProjectIdAndUserId(projectId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found in project"));
    }
}
