package com.code_space.code_space_editor.project_managment.service.interfaces;

import java.util.List;
import com.code_space.code_space_editor.project_managment.entity.sql.File;
import com.code_space.code_space_editor.project_managment.dto.file.CreateFileDTO;
import com.code_space.code_space_editor.project_managment.entity.sql.Branch;
import com.code_space.code_space_editor.project_managment.entity.sql.Commit;

public interface FileServiceInterface {
    List<File> getByCommit(Long commitId);

    File createFile(Long projectId, Branch branch, Commit commit, CreateFileDTO fileDTO);

    File updateFileInfo(Long fileId, String name);

    void forkFiles(Long projectId, Long branhcId, Commit baseCommit, Commit newCommit);

    void deleteFilesByBranch(Branch branch);

    List<File> getAllByCommitId(Long commitId);

    String getFileContent(File file);

    String getFileContent(Long fileId);
}
