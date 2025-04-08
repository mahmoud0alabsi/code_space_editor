package com.code_space.code_space_editor.project_managment.dto.merge;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConflictDTO {
    private String fileName;
    private String filePath;
    private List<LineConflictDTO> lineConflicts; // Specific conflicting lines
}