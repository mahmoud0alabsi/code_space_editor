package com.code_space.code_space_editor.collaborative_coding.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Service;
import com.code_space.code_space_editor.collaborative_coding.dto.BranchSyncDTO;
import com.code_space.code_space_editor.collaborative_coding.dto.FileSyncDTO;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SessionStateService {
    private final Map<String, List<BranchSyncDTO>> projectBranches = new ConcurrentHashMap<>();
    private final Map<String, List<FileSyncDTO>> projectFiles = new ConcurrentHashMap<>();

    public void addBranch(String projectId, BranchSyncDTO branch) {
        projectBranches.computeIfAbsent(projectId, k -> new ArrayList<>()).add(branch);
    }

    public void addFile(String projectId, FileSyncDTO file) {
        projectFiles.computeIfAbsent(projectId, k -> new ArrayList<>()).add(file);
    }

    public List<BranchSyncDTO> getBranches(String projectId) {
        return projectBranches.getOrDefault(projectId, Collections.emptyList());
    }

    public List<FileSyncDTO> getFiles(String projectId) {
        return projectFiles.getOrDefault(projectId, Collections.emptyList());
    }
}
