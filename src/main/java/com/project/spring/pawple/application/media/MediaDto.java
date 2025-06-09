package com.project.spring.pawple.application.media;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MediaDto {
    private Long id;
    private String fileName;
    private String fileUrl;
    private String mediaType;

    public static MediaDto fromEntity(MediaEntity entity) {
        return MediaDto.builder()
                .id(entity.getId())
                .fileName(entity.getFileName())
                .fileUrl(entity.getFileUrl())
                .mediaType(entity.getMediaType())
                .build();
    }
}

