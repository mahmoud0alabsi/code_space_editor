package com.code_space.code_space_editor.project_managment.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.code_space.code_space_editor.project_managment.entity.sql.Branch;

@Repository
public interface BranchRepository extends JpaRepository<Branch, Long> {
    Branch findByIdAndProjectId(Long branchId, Long projectId);
    List<Branch> findByProjectId(Long projectId);

    boolean existsByNameAndProjectId(String name, Long projectId);
}
