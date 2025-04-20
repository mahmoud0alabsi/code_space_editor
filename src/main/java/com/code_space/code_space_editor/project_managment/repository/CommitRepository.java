package com.code_space.code_space_editor.project_managment.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.code_space.code_space_editor.project_managment.entity.sql.Commit;

@Repository
public interface CommitRepository extends JpaRepository<Commit, Long> {
    Optional<Commit> findByIdAndBranchId(Long commitId, Long branchId);

    List<Commit> findByBranchId(Long branchId);

    void deleteByBranchId(Long branchId);
}
