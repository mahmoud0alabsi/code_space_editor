package com.code_space.code_space_editor.project_managment.dto.merge;

import com.code_space.code_space_editor.project_managment.entity.sql.Commit;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MergeResultDTO {
    private boolean success;
    private Commit mergedCommit; // The new commit if successful
    private List<ConflictDTO> conflicts; // List of conflicts
}
