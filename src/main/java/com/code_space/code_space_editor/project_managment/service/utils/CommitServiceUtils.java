package com.code_space.code_space_editor.project_managment.service.utils;

import java.util.List;

import com.code_space.code_space_editor.project_managment.entity.sql.Commit;
import com.code_space.code_space_editor.project_managment.repository.CommitRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import com.code_space.code_space_editor.exceptions.ResourceNotFoundException;

@Service
@RequiredArgsConstructor
public class CommitServiceUtils {
    private final CommitRepository commitRepository;

    public Commit getLatestCommit(Long branchId) {
        List<Commit> commits = commitRepository.findByBranchId(branchId);
        if (commits.isEmpty()) {
            return null;
        }
        return commits.stream()
                .max((commit1, commit2) -> commit1.getCreatedAt().compareTo(commit2.getCreatedAt()))
                .orElseThrow(() -> new ResourceNotFoundException("No commits found for branch " + branchId));
    }

}
