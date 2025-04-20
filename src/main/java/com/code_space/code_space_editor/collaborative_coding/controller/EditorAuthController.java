package com.code_space.code_space_editor.collaborative_coding.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.code_space.code_space_editor.collaborative_coding.dto.AccessSessionRequestDTO;
import com.code_space.code_space_editor.project_managment.authorizations.PermissionService;
import com.code_space.code_space_editor.project_managment.dto.ProjectMemberDTO;
import com.code_space.code_space_editor.project_managment.entity.sql.ProjectMember;
import com.code_space.code_space_editor.project_managment.mapper.ProjectMemberMapperImpl;
import com.code_space.code_space_editor.project_managment.service.ProjectMemberService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/editor/auth")
public class EditorAuthController {

    private final PermissionService permissionService;
    private final ProjectMemberService projectMemberService;
    private final ProjectMemberMapperImpl projectMemberMapper;

    @PostMapping("/check-access")
    public ResponseEntity<ProjectMemberDTO> checkUserSessionAuth(
            @Valid @RequestBody AccessSessionRequestDTO requestBody) {
        Long projectId = requestBody.getProjectId();
        Long branchId = requestBody.getBranchId();

        if (permissionService.hasBranchPermission(projectId, branchId, "VIEWER")) {
            ProjectMember member = projectMemberService.getProjectMemberOrThrow(projectId);
            return ResponseEntity.ok(projectMemberMapper.toDTO(member));
        } else {
            return ResponseEntity.status(403).build();
        }
    }
}
