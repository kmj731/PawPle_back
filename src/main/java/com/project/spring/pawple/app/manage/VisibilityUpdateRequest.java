package com.project.spring.pawple.app.manage;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class VisibilityUpdateRequest {
    private Long id;
    private String title;
    private String content;
    private Boolean isPublic;
}
