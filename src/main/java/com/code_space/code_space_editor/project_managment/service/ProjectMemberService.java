package com.code_space.code_space_editor.project_managment.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.code_space.code_space_editor.project_managment.entity.enums.ProjectRole;
import com.code_space.code_space_editor.project_managment.entity.sql.Project;
import com.code_space.code_space_editor.project_managment.entity.sql.ProjectMember;
import com.code_space.code_space_editor.project_managment.repository.ProjectMemberRepository;

import jakarta.transaction.Transactional;

import com.code_space.code_space_editor.exceptions.ResourceNotFoundException;
import com.code_space.code_space_editor.project_managment.repository.ProjectRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProjectMemberService {
    private final ProjectMemberRepository projectMemberRepository;
    private final ProjectRepository projectRepository;

    @Transactional
    public ProjectMember add(Long projectId, Long userId, ProjectRole role) {
        // Check if the project exists
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found"));

        // Check if the user is already a member of the project
        if (projectMemberRepository.existsByProjectIdAndUserId(projectId, userId)) {
            throw new ResourceNotFoundException("User is already a member of the project");
        }

        // Create a new ProjectMember entity
        ProjectMember member = new ProjectMember();
        member.setProject(project);
        member.setUserId(userId);
        member.setRole(role);
        return projectMemberRepository.save(member);
    }

    @Transactional
    public List<ProjectMember> getAllByProjectId(Long projectId) {
        return projectMemberRepository.findAllByProjectId(projectId);
    }

    @Transactional
    public void removeMemberFromProject(Long projectId, Long memberId) {
        // Find the member to remove
        ProjectMember member = projectMemberRepository.findByProjectIdAndUserId(projectId, memberId)
                .orElseThrow(() -> new ResourceNotFoundException("Member not found"));

        // Remove the member from the project
        projectMemberRepository.delete(member);
    }

    @Transactional
    public ProjectMember changeRole(Long projectId, Long userId, ProjectRole role) {
        ProjectMember currentMember = projectMemberRepository
                .findByProjectIdAndUserId(projectId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Member not found"));

        // Update the role
        currentMember.setRole(role);
        return projectMemberRepository.save(currentMember);
    }
}
