package com.code_space.code_space_editor.project_managment.dto.merge;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LineConflictDTO {
    private int lineNumber;
    private String baseContent; // Content from the base version
    private String sourceContent; // Content from the source branch
    private String targetContent; // Content from the target branch
}