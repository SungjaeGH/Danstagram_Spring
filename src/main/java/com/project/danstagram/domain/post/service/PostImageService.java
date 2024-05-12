package com.project.danstagram.domain.post.service;

import com.project.danstagram.domain.post.dto.CreatePostImageDto;
import com.project.danstagram.domain.post.entity.PostImage;
import com.project.danstagram.global.file.ConstUtil;
import com.project.danstagram.global.file.FileUploadUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class PostImageService {

    private final FileUploadUtil fileUploadUtil;

    public List<PostImage> setImageList(List<MultipartFile> imageFiles) throws IOException {
        List<PostImage> list = new ArrayList<>();
        int fileFlag = ConstUtil.UPLOAD_IMAGE_FLAG;

        fileUploadUtil.checkDirectory(fileFlag);

        List<Map<String, Object>> mapList = fileUploadUtil.multiFileUpload(imageFiles, fileFlag);

        for (Map<String, Object> imageInfo : mapList) {
            String storedImagePath = (String)imageInfo.get("fileName");
            String originImagePath = (String)imageInfo.get("originalFileName");
            CreatePostImageDto createPostImageDto = new CreatePostImageDto(storedImagePath, originImagePath);

            list.add(createPostImageDto.toEntity());
        }

        return list;
    }
}
