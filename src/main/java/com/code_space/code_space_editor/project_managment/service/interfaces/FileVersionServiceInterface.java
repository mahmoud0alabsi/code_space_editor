package com.code_space.code_space_editor.project_managment.service.interfaces;

import java.util.List;
import com.code_space.code_space_editor.project_managment.entity.sql.File;
import com.code_space.code_space_editor.project_managment.entity.sql.FileVersion;

public interface FileVersionServiceInterface {
    FileVersion createNewVersion(Long projectId, Long branchId, File file, String content, String message,
            Long versionNumber);

    String readFileVersionContent(FileVersion fileVersion);

    List<FileVersion> getFileVersions(Long fileId);

    FileVersion getLatestVersion(Long fileId);

    FileVersion getVersionByFileIdAndVersionNumber(Long fileId, Long versionNumber);
}
