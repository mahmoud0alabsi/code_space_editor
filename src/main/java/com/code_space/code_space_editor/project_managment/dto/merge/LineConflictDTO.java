package com.code_space.code_space_editor.project_managment.dto.merge;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LineConflictDTO {
    private int lineNumber;
    private String baseContent;
    private String sourceContent;
    private String targetContent;
}