package com.code_space.code_space_editor.project_managment.repository;

import org.springframework.stereotype.Repository;

import com.code_space.code_space_editor.project_managment.entity.sql.FileVersion;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

@Repository
public interface FileVersionRepository extends JpaRepository<FileVersion, Long> {
    List<FileVersion> findByFileId(Long fileId); // Get all versions for a file

    Optional<FileVersion> findTopByFileIdOrderByVersionNumberDesc(Long fileId); // Get the latest version of a file by
                                                                                // its fileId

    Optional<FileVersion> findByFileIdAndVersionNumber(Long fileId, Long versionNumber);
}

// @Repository
// public interface FileVersionRepository extends MongoRepository<FileVersion,
// String> {
// List<FileVersion> findByFileIdOrderByVersionNumberDesc(String fileId);

// // get the latest version of a file by its fileId
// FileVersion findTopByFileIdOrderByVersionNumberDesc(String fileId);
// }
