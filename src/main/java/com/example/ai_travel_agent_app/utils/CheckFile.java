package com.example.ai_travel_agent_app.utils;


import org.springframework.web.multipart.MultipartFile;
import org.apache.commons.io.FilenameUtils;
import java.util.Arrays;

public class CheckFile {

    public static boolean isImageFile(MultipartFile file) {
        String fileExtension = FilenameUtils.getExtension(file.getOriginalFilename());
        return Arrays.asList(new String[] {"png","jpg","jpeg", "bmp"})
                .contains(fileExtension.trim().toLowerCase());
    }
}
