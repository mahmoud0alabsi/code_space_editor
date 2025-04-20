package com.code_space.code_space_editor.project_managment.dto.commit;

import java.time.Instant;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommitDTO {
    private Long id;
    private Long author;
    private String authorName;
    private String message;
    private Instant createdAt;
    private Long parentCommit;
}
