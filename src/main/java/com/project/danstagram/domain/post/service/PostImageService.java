package com.project.danstagram.domain.post.service;

import com.project.danstagram.domain.post.dto.CreatePostImageDto;
import com.project.danstagram.domain.post.dto.PostImageResponseDto;
import com.project.danstagram.domain.post.entity.PostImage;
import com.project.danstagram.domain.post.exception.PostNotFoundException;
import com.project.danstagram.domain.post.repository.PostImageRepository;
import com.project.danstagram.global.file.ConstUtil;
import com.project.danstagram.global.file.FileUploadUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostImageService {

    private final FileUploadUtil fileUploadUtil;
    private final PostImageRepository postImageRepository;

    public List<PostImage> setImageList(Long writerIdx, List<MultipartFile> imageFiles) throws IOException {
        List<PostImage> list = new ArrayList<>();
        int fileFlag = ConstUtil.UPLOAD_IMAGE_FLAG;

        fileUploadUtil.checkDirectory(fileFlag, true);

        List<Map<String, Object>> mapList = fileUploadUtil.multiFileUpload(writerIdx, imageFiles, fileFlag);

        for (Map<String, Object> imageInfo : mapList) {
            String storedImagePath = (String)imageInfo.get("fileName");
            String originImagePath = (String)imageInfo.get("originalFileName");
            CreatePostImageDto createPostImageDto = new CreatePostImageDto(storedImagePath, originImagePath);

            list.add(createPostImageDto.toEntity());
        }

        return list;
    }

    public List<PostImageResponseDto> findImageList(Long postIdx) {
        List<PostImage> findPostImage = postImageRepository.findByPostIdx(postIdx);
        if (findPostImage.isEmpty()) {
            throw new PostNotFoundException("해당 게시글의 이미지를 찾을 수 없습니다. Idx: " + postIdx);
        }

        return findPostImage.stream()
                .map(postImage -> new PostImageResponseDto(
                        postImage.getPostImageIdx(),
                        postImage.getStoreImageFile()))
                .collect(Collectors.toList());
    }

    public Map<String, String> getImagesList(Long postIdx) {

        String uploadPath = fileUploadUtil.getUploadPath(ConstUtil.UPLOAD_IMAGE_FLAG);

        File path = new File(uploadPath);
        File[] fileList = path.listFiles();
        Map<String, String> matchesMap = new HashMap<>();

        if (fileList.length > 0) {
            for (File file : fileList) {
                String fileName = file.getName();

                // 파일의 첫번째 문자가 postIdx가 아닐 경우, skip
                if (!fileName.startsWith(postIdx.toString())) {
                    continue;
                }

                try (FileInputStream fileInputStream = new FileInputStream(file)) {
                    byte[] fileToBytes = fileInputStream.readAllBytes();
                    String encodeFile = Base64.getEncoder().encodeToString(fileToBytes);

                    matchesMap.put(fileName, encodeFile);

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return matchesMap;
    }
}
