package com.code_space.code_space_editor.project_managment.controller;

import java.util.List;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.GetMapping;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.code_space.code_space_editor.project_managment.dto.ProjectMemberDTO;
import com.code_space.code_space_editor.project_managment.dto.project_member.AddProjectMemberDTO;
import com.code_space.code_space_editor.project_managment.mapper.ProjectMemberMapper;
import com.code_space.code_space_editor.project_managment.service.ProjectMemberService;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@RequestMapping("/api/v1/projects/{projectId}/members")
@Tag(name = "Project Member Management", description = "Project member management operations")
public class ProjectMemberController {

        private final ProjectMemberService projectMemberService;
        private final ProjectMemberMapper projectMemberMapper;

        @PostMapping
        @PreAuthorize("@permissionService.hasProjectPermission(#projectId, 'OWNER')")
        @Operation(summary = "Add a member to a project")
        public ResponseEntity<String> addMember(
                        @PathVariable Long projectId,
                        @RequestBody @Valid AddProjectMemberDTO dto) {
                projectMemberService.add(
                                projectId,
                                dto.getUsername(),
                                dto.getRole());
                return ResponseEntity.status(HttpStatus.CREATED).body(
                                "Member added successfully");
        }

        @DeleteMapping("/{memberName}")
        @PreAuthorize("@permissionService.hasProjectPermission(#projectId, 'OWNER')")
        @Operation(summary = "Remove a member from a project")
        public ResponseEntity<String> removeMember(
                        @PathVariable Long projectId,
                        @PathVariable String memberName) {
                projectMemberService.removeMemberFromProject(projectId, memberName);
                return ResponseEntity.ok("Member removed successfully");
        }

        @PutMapping
        @PreAuthorize("@permissionService.hasProjectPermission(#projectId, 'OWNER')")
        @Operation(summary = "Change a member's role in the project")
        public ResponseEntity<String> changeMemberRole(
                        @PathVariable Long projectId,
                        @RequestBody @Valid AddProjectMemberDTO dto) {

                projectMemberService.changeRole(
                                projectId,
                                dto.getUsername(),
                                dto.getRole());
                return ResponseEntity.ok("Member role updated successfully");
        }

        @GetMapping
        @PreAuthorize("@permissionService.hasProjectPermission(#projectId, 'VIEWER')")
        @Operation(summary = "Get all members of a project")
        public ResponseEntity<List<ProjectMemberDTO>> getAllMembers(
                        @PathVariable Long projectId) {
                List<ProjectMemberDTO> members = projectMemberService.getAllMembers(projectId)
                                .stream()
                                .map(projectMemberMapper::toDTO)
                                .toList();
                return ResponseEntity.ok(members);
        }
}
