package com.code_space.code_space_editor.project_managment.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.code_space.code_space_editor.project_managment.entity.sql.File;

import java.util.List;

import org.springframework.data.jpa.repository.Query;

import com.code_space.code_space_editor.project_managment.entity.enums.ProjectRole;

@Repository
public interface FileRepository extends JpaRepository<File, Long> {
        List<File> findByCommitId(Long commitId);

        boolean existsByCommitIdAndName(Long commitId, String name);

        /*
         * Check if a file exists in a project and branch for a specific user with a
         * specific role. This is useful for authorization checks to ensure that the
         * user has the right permissions to access or modify the file.
         */
        @Query("SELECT COUNT(f) > 0 FROM File f " +
                        "JOIN f.branch b " +
                        "JOIN b.project p " +
                        "JOIN p.members m " +
                        "WHERE p.id = :projectId " +
                        "AND b.id = :branchId " +
                        "AND f.id = :fileId " +
                        "AND m.userId = :userId " +
                        "AND m.role IN :allowedRoles")
        boolean existsByProjectBranchFileAndUserWithRoles(
                        @Param("projectId") Long projectId,
                        @Param("branchId") Long branchId,
                        @Param("fileId") Long fileId,
                        @Param("userId") Long userId,
                        @Param("allowedRoles") List<ProjectRole> allowedRoles);

        @Query("SELECT f FROM File f WHERE f.branch.id = :branchId")
        List<File> findByBranchId(Long branchId);
}
