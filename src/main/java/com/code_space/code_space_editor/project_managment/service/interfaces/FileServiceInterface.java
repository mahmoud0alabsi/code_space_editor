package com.code_space.code_space_editor.project_managment.service.interfaces;

import java.util.List;
import com.code_space.code_space_editor.project_managment.entity.sql.File;
import com.code_space.code_space_editor.project_managment.entity.sql.FileVersion;
import com.code_space.code_space_editor.project_managment.entity.sql.Branch;

public interface FileServiceInterface {

    File createFile(Long projectId, Long branchId, String fileName, String language, String extension);

    FileVersion createFileVersion(Long fileId, String newContent, String message);

    File updateFileInfo(Long fileId, String name, String language, String extension);

    List<File> forkFiles(Long branchId, Branch newBranch);

    List<File> getByBranch(Long branchId);

    List<File> getAllByBranchId(Long branchId);

    List<FileVersion> getAllFileVersions(Long fileId);

    String getFileContent(Long fileId);

    String getSpecificVersionContent(Long fileId, Long versionNumber);
}
