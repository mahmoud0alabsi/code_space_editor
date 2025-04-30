package com.code_space.code_space_editor.project_managment.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.code_space.code_space_editor.auth.entity.User;
import com.code_space.code_space_editor.auth.repository.UserRepository;
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
    private final UserRepository userRepository;
    private final ProjectMemberRepository projectMemberRepository;
    private final ProjectRepository projectRepository;
    private final AuthUtils authUtils;

    @Transactional
    public ProjectMember add(Long projectId, String username, ProjectRole role) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found"));

        User newMember = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User '" + username + "' not found"));

        if (projectMemberRepository.existsByProjectIdAndUserId(projectId, newMember.getId())) {
            throw new IllegalArgumentException("User is already a member of the project");
        }

        ProjectMember member = ProjectMember.builder()
                .project(project)
                .userId(newMember.getId())
                .username(newMember.getUsername())
                .role(role)
                .build();
        return projectMemberRepository.save(member);
    }

    @Transactional
    public void removeMemberFromProject(Long projectId, String username) {
        projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found"));

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User '" + username + "' not found"));

        ProjectMember member = projectMemberRepository.findByProjectIdAndUserId(projectId, user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Member not found"));

        projectMemberRepository.delete(member);
    }

    @Transactional
    public ProjectMember changeRole(Long projectId, String username, ProjectRole role) {
        projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found"));

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User '" + username + "' not found"));

        ProjectMember member = projectMemberRepository
                .findByProjectIdAndUserId(projectId, user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Member not found"));

        member.setRole(role);
        return projectMemberRepository.save(member);
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
