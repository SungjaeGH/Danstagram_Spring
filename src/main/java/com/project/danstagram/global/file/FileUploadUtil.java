package com.project.danstagram.global.file;

import com.project.danstagram.global.time.TimeFormat;
import com.project.danstagram.global.time.TimeUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.*;

@Component
public class FileUploadUtil {
    private static final Logger logger = LoggerFactory.getLogger(FileUploadUtil.class);

    public List<Map<String, Object>> multiFileUpload(Long writerIdx, List<MultipartFile> fileMap, int pathFlag) throws IllegalStateException, IOException {

        List<Map<String, Object>> list = new ArrayList<>();

        for (MultipartFile multipartFile : fileMap) {
            if (!multipartFile.isEmpty()) {
                long fileSize = multipartFile.getSize();	        // 파일 크기
                String oName = multipartFile.getOriginalFilename();	// 원래 파일명

                // 변경된 파일이름 구하기
                String fileName = getUniqueFileName(writerIdx, oName);

                //업로드할 폴더 구하기
                String uploadPath = getUploadPath(pathFlag);

                // 파일 업로드 처리
                File file = new File(uploadPath, fileName);
                multipartFile.transferTo(file);

                // 업로드된 파일 정보 저장
                // 1. Map에 저장
                Map<String, Object> resultMap = new HashMap<>();
                resultMap.put("fileName", fileName);
                resultMap.put("fileSize", fileSize);
                resultMap.put("originalFileName", oName);

                // 2. 여러 개의 Map을 List에 저장
                list.add(resultMap);
            }
        }

        return list;
    }

    public String getUniqueFileName(Long writerIdx, String fileName) {
        // 파일명이 중복될 경우 파일이름 변경하기
        // 파일명 앞 : 게시물 작성자 Index 붙이기
        // 파일명 뒤 : 현재시간(년원일 시분초 밀리초) 붙이기
        // ex) a.jpg => 1_a_20220602113820123.jpg

        // 순수 파일명만 구하기 => a
        int idx = fileName.lastIndexOf(".");
        String fileNm = fileName.substring(0, idx);

        // 확장자 구하기 => .jpg
        String ext = fileName.substring(idx);

        // 현재시간 구하기
        TimeUtil timeUtil = new TimeUtil();
        String today = timeUtil.getCurrTime(TimeFormat.TimeFormat2);

        String result = writerIdx + "_" + fileNm + "_" + today + ext;
        logger.info("변경된 파일명 : " + result);

        return result;
    }

    public String getUploadPath(int pathFlag) {
        String path = "";

        switch (pathFlag) {
            case ConstUtil.UPLOAD_FILE_FLAG -> path = ConstUtil.FILE_UPLOAD_FULL_PATH;
            case ConstUtil.UPLOAD_IMAGE_FLAG -> path = ConstUtil.IMAGE_FILE_UPLOAD_FULL_PATH;
        }

        logger.info("업로드 경로 : " + path);

        return path;
    }

    public boolean checkDirectory(int pathFlag, boolean isCreateDir) {
        String path = null;

        switch (pathFlag) {
            case ConstUtil.UPLOAD_FILE_FLAG -> path = ConstUtil.FILE_PATH + ConstUtil.FILE_UPLOAD_PATH;
            case ConstUtil.UPLOAD_IMAGE_FLAG -> path = ConstUtil.FILE_PATH + ConstUtil.IMAGE_FILE_UPLOAD_PATH;
        }

        Path directoryPath = Paths.get(path);

        if (isCreateDir) {
            try {
                Files.createDirectories(directoryPath);
                logger.info("디렉토리 생성 : " + directoryPath);
                return true;

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (Files.exists(directoryPath)) {
            return true;
        }

        return false;
    }
}
