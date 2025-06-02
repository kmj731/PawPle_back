package com.project.spring.skillstack.utility;

import net.coobird.thumbnailator.Thumbnails;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ImageUtil {

    public static Map<String, String> saveImageAndThumbnail(MultipartFile file, String folderName) {
        try {
            if (!file.getContentType().startsWith("image/")) {
                throw new IllegalArgumentException("이미지 파일만 업로드 가능합니다.");
            }

            String uuid = UUID.randomUUID().toString();
            String originalFilename = file.getOriginalFilename();
            String filename = uuid + "_" + originalFilename;

            String basePath = System.getProperty("user.dir") + "/uploads/" + folderName + "/";
            String thumbPath = System.getProperty("user.dir") + "/uploads/thumb/";

            File imageDest = Paths.get(basePath, filename).toFile();
            imageDest.getParentFile().mkdirs();
            file.transferTo(imageDest);

            // 썸네일 생성
            String thumbFilename = "thumb_" + filename;
            File thumbDest = Paths.get(thumbPath, thumbFilename).toFile();
            thumbDest.getParentFile().mkdirs();

            Thumbnails.of(imageDest)
                      .size(150, 150)
                      .outputFormat("jpg")
                      .toFile(thumbDest);

            Map<String, String> result = new HashMap<>();
            result.put("imageUrl", "/" + folderName + "/" + filename);
            result.put("thumbnailUrl", "/thumb/" + thumbFilename);
            return result;

        } catch (IOException e) {
            throw new RuntimeException("이미지 저장 실패", e);
        }
    }
}
