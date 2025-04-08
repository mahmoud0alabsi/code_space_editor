package com.code_space.code_space_editor.project_managment.controller;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.code_space.code_space_editor.project_managment.dto.ProjectMemberDTO;
import com.code_space.code_space_editor.project_managment.dto.project_member.AddProjectMemberDTO;
import com.code_space.code_space_editor.project_managment.service.ProjectMemberService;

import lombok.RequiredArgsConstructor;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;

@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/api/v1/projects/{projectId}/members")
@RequiredArgsConstructor
public class ProjectMemberController {

        private final ProjectMemberService projectMemberService;

        // Add a member to a project with a specific role
        @PostMapping
        @PreAuthorize("@permissionService.hasProjectRole(#projectId, 'OWNER')")
        public ResponseEntity<String> addMember(
                        @PathVariable Long projectId,
                        @RequestBody @Valid AddProjectMemberDTO dto) {
                projectMemberService.add(
                                projectId,
                                dto.getUserId(),
                                dto.getRole());
                return ResponseEntity.status(HttpStatus.CREATED).body(
                                "Member added successfully");
        }

        // Remove a member from a project
        @DeleteMapping("/{memberId}")
        @PreAuthorize("@permissionService.hasProjectRole(#projectId, 'OWNER')")
        public ResponseEntity<String> removeMember(
                        @PathVariable Long projectId,
                        @PathVariable Long memberId) {
                projectMemberService.removeMemberFromProject(projectId, memberId);
                return ResponseEntity.ok("Member removed successfully");
        }

        // Change a member's role in the project
        @PutMapping
        @PreAuthorize("@permissionService.hasProjectRole(#projectId, 'OWNER')")
        public ResponseEntity<String> changeMemberRole(
                        @PathVariable Long projectId,
                        @RequestBody @Valid ProjectMemberDTO dto) {

                projectMemberService.changeRole(
                                projectId,
                                dto.getUserId(),
                                dto.getRole());

                // Convert to DTO and return
                return ResponseEntity.ok("Member role updated successfully");
        }
}
