package com.project.spring.pawple.app.comment;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@AllArgsConstructor
@Getter
@Setter
public class MentionUserDto {
    private Long id;
    private String nickname;
}

