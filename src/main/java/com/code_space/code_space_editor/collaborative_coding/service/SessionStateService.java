package com.code_space.code_space_editor.collaborative_coding.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Service;
import com.code_space.code_space_editor.collaborative_coding.dto.FileSyncDTO;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SessionStateService {
    private final Map<String, List<FileSyncDTO>> projectFiles = new ConcurrentHashMap<>();

    public void addFile(String projectId, FileSyncDTO file) {
        projectFiles.computeIfAbsent(projectId, k -> new ArrayList<>()).add(file);
    }

    public List<FileSyncDTO> getFiles(String projectId) {
        return projectFiles.getOrDefault(projectId, Collections.emptyList());
    }
}
