package com.code_space.code_space_editor.project_managment.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;

import com.code_space.code_space_editor.exceptions.ResourceNotFoundException;
import com.code_space.code_space_editor.project_managment.dto.merge.ConflictDTO;
import com.code_space.code_space_editor.project_managment.dto.merge.LineConflictDTO;
import com.code_space.code_space_editor.project_managment.dto.merge.MergeResultDTO;
import com.code_space.code_space_editor.project_managment.entity.sql.Branch;
import com.code_space.code_space_editor.project_managment.entity.sql.Commit;
import com.code_space.code_space_editor.project_managment.entity.sql.File;
import com.code_space.code_space_editor.project_managment.repository.BranchRepository;
import com.code_space.code_space_editor.project_managment.repository.CommitRepository;
import com.code_space.code_space_editor.project_managment.service.FileStorageService;
import com.code_space.code_space_editor.project_managment.service.FileService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MergeBranchService {
    private final CommitRepository commitRepository;
    private final BranchRepository branchRepository;
    private final FileService fileService;
    private final FileStorageService fileStorageService;

    /**
     * Perform a three-way merge from source branch into target branch.
     */
    @Transactional
    public MergeResultDTO mergeBranch(Long targetBranchId, Long sourceBranchId, Long authorId) {
        return null;
    }
    // // Fetch branches
    // Branch targetBranch = branchRepository.findById(targetBranchId)
    // .orElseThrow(() -> new ResourceNotFoundException("Target branch not found
    // with ID: " + targetBranchId));
    // Branch sourceBranch = branchRepository.findById(sourceBranchId)
    // .orElseThrow(() -> new ResourceNotFoundException("Source branch not found
    // with ID: " + sourceBranchId));

    // // Ensure branches belong to the same project
    // if
    // (!targetBranch.getProject().getId().equals(sourceBranch.getProject().getId()))
    // {
    // throw new IllegalArgumentException("Cannot merge branches from different
    // projects");
    // }

    // // Get latest commits
    // Commit targetLatestCommit = getLatestCommit(targetBranchId);
    // Commit sourceLatestCommit = getLatestCommit(sourceBranchId);
    // if (sourceLatestCommit == null) {
    // throw new IllegalStateException("Source branch has no commits to merge");
    // }

    // // Find the common ancestor (base commit)
    // Commit baseCommit = findCommonAncestor(targetBranch, sourceBranch);
    // if (baseCommit == null) {
    // throw new IllegalStateException("No common ancestor found between branches");
    // }

    // // Get files for all three commits
    // List<File> baseFiles = baseCommit != null ?
    // fileService.getByCommit(baseCommit.getId())
    // : Collections.emptyList();
    // List<File> sourceFiles = fileService.getByCommit(sourceLatestCommit.getId());
    // List<File> targetFiles = targetLatestCommit != null ?
    // fileService.getByCommit(targetLatestCommit.getId())
    // : Collections.emptyList();

    // // Detect conflicts
    // List<ConflictDTO> conflicts = detectConflicts(baseFiles, sourceFiles,
    // targetFiles);
    // if (!conflicts.isEmpty()) {
    // return new MergeResultDTO(false, null, conflicts); // Return conflicts if any
    // }

    // // No conflicts: proceed with merge
    // Commit mergeCommit = Commit.builder()
    // .author(authorId)
    // .branch(targetBranch)
    // .message("Merged branch " + sourceBranch.getName() + " into " +
    // targetBranch.getName())
    // .createdAt(Instant.now())
    // .parentCommit(targetLatestCommit != null ? targetLatestCommit.getId() : null)
    // .build();
    // mergeCommit = commitRepository.save(mergeCommit);

    // // Apply changes: Fork files from source commit to merge commit
    // fileService.forkFiles(sourceLatestCommit, mergeCommit);

    // // Update target branch metadata
    // targetBranch.setUpdatedAt(Instant.now());
    // branchRepository.save(targetBranch);

    // return new MergeResultDTO(true, mergeCommit, Collections.emptyList());
    // }

    // /**
    // * Find the common ancestor between two branches.
    // * Simplified: Use baseBranchId and traverse parent commits if needed.
    // */
    // private Commit findCommonAncestor(Branch targetBranch, Branch sourceBranch) {
    // // If sourceBranch was forked from targetBranch
    // if (sourceBranch.getBaseBranchId() != null &&
    // sourceBranch.getBaseBranchId().equals(targetBranch.getId())) {
    // Commit sourceBaseCommit = commitRepository
    // .findById(sourceBranch.getCommits().iterator().next().getParentCommit())
    // .orElse(null);
    // if (sourceBaseCommit != null
    // && targetBranch.getCommits().stream().anyMatch(c ->
    // c.getId().equals(sourceBaseCommit.getId()))) {
    // return sourceBaseCommit;
    // }
    // }

    // // More complex: Traverse commit history (simplified here)
    // List<Commit> targetCommits = getAllCommits(targetBranch.getId());
    // List<Commit> sourceCommits = getAllCommits(sourceBranch.getId());
    // for (Commit sourceCommit : sourceCommits) {
    // if (targetCommits.stream().anyMatch(tc ->
    // tc.getId().equals(sourceCommit.getId()))) {
    // return sourceCommit; // First common commit
    // }
    // }
    // return null; // No common ancestor found
    // }

    // /**
    // * Detect conflicts between base, source, and target files.
    // */
    // private List<ConflictDTO> detectConflicts(List<File> baseFiles, List<File>
    // sourceFiles, List<File> targetFiles) {
    // List<ConflictDTO> conflicts = new ArrayList<>();
    // Map<String, File> baseMap =
    // baseFiles.stream().collect(Collectors.toMap(File::getName, f -> f));
    // Map<String, File> sourceMap =
    // sourceFiles.stream().collect(Collectors.toMap(File::getName, f -> f));
    // Map<String, File> targetMap =
    // targetFiles.stream().collect(Collectors.toMap(File::getName, f -> f));

    // // Check each file in source and target
    // for (String fileName : sourceMap.keySet()) {
    // File sourceFile = sourceMap.get(fileName);
    // File targetFile = targetMap.get(fileName);
    // File baseFile = baseMap.get(fileName);

    // if (targetFile != null && baseFile != null) {
    // // File exists in both source and target, and changed since base
    // if (!isFileContentEqual(sourceFile, baseFile) &&
    // !isFileContentEqual(targetFile, baseFile)) {
    // ConflictDTO conflict = detectLineConflicts(baseFile, sourceFile, targetFile);
    // if (!conflict.getLineConflicts().isEmpty()) {
    // conflicts.add(conflict);
    // }
    // }
    // }
    // }

    // return conflicts;
    // }

    // /**
    // * Check if two files have the same content.
    // */
    // private boolean isFileContentEqual(File file1, File file2) {
    // String content1 = fileStorageService.readFile(file1.getPath());
    // String content2 = fileStorageService.readFile(file2.getPath());
    // return content1.equals(content2);
    // }

    // /**
    // * Detect line-level conflicts for a single file.
    // */
    // private ConflictDTO detectLineConflicts(File baseFile, File sourceFile, File
    // targetFile) {
    // String baseContent = fileStorageService.readFile(baseFile.getPath());
    // String sourceContent = fileStorageService.readFile(sourceFile.getPath());
    // String targetContent = fileStorageService.readFile(targetFile.getPath());

    // List<String> baseLines = List.of(baseContent.split("\n"));
    // List<String> sourceLines = List.of(sourceContent.split("\n"));
    // List<String> targetLines = List.of(targetContent.split("\n"));

    // List<LineConflictDTO> lineConflicts = new ArrayList<>();
    // int maxLines = Math.max(Math.max(baseLines.size(), sourceLines.size()),
    // targetLines.size());

    // for (int i = 0; i < maxLines; i++) {
    // String baseLine = i < baseLines.size() ? baseLines.get(i) : "";
    // String sourceLine = i < sourceLines.size() ? sourceLines.get(i) : "";
    // String targetLine = i < targetLines.size() ? targetLines.get(i) : "";

    // if (!sourceLine.equals(baseLine) && !targetLine.equals(baseLine) &&
    // !sourceLine.equals(targetLine)) {
    // lineConflicts.add(new LineConflictDTO(i + 1, baseLine, sourceLine,
    // targetLine));
    // }
    // }

    // return new ConflictDTO(sourceFile.getName(), sourceFile.getPath(),
    // lineConflicts);
    // }
}