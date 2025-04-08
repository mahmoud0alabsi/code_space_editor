package com.code_space.code_space_editor.project_managment.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.code_space.code_space_editor.project_managment.entity.sql.Project;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {}
