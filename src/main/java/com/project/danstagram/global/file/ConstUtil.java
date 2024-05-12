package com.project.danstagram.global.file;

public interface ConstUtil {
    // 파일 저장 경로
    String FILE_PATH = "C:\\Users\\SungJae\\Danstagram_Spring\\image\\";
    String FILE_UPLOAD_PATH = "file_upload";
    String FILE_UPLOAD_FULL_PATH = FILE_PATH + FILE_UPLOAD_PATH;

    // 이미지 저장 경로
    String IMAGE_FILE_UPLOAD_PATH = "image_upload";
    String IMAGE_FILE_UPLOAD_FULL_PATH = FILE_PATH + IMAGE_FILE_UPLOAD_PATH;

    // 파일, 이미지 구분 값
    int UPLOAD_FILE_FLAG = 1;	// 파일 업로드
    int UPLOAD_IMAGE_FLAG = 2;	// 이미지 업로드
}
