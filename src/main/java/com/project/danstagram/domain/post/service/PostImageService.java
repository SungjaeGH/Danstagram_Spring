package com.project.danstagram.domain.post.service;

import com.project.danstagram.domain.post.dto.CreatePostImageDto;
import com.project.danstagram.domain.post.dto.PostImageResponseDto;
import com.project.danstagram.domain.post.entity.PostImage;
import com.project.danstagram.domain.post.exception.PostImageNotFoundException;
import com.project.danstagram.domain.post.repository.PostImageRepository;
import com.project.danstagram.global.file.ConstUtil;
import com.project.danstagram.global.file.FileUploadUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.*;

@Service
@RequiredArgsConstructor
public class PostImageService {

    private final FileUploadUtil fileUploadUtil;
    private final PostImageRepository postImageRepository;

    public List<PostImage> setImageList(Long targetIdx, List<MultipartFile> imageFiles) throws IOException {
        List<PostImage> list = new ArrayList<>();
        int fileFlag = ConstUtil.UPLOAD_IMAGE_FLAG;

        fileUploadUtil.checkDirectory(fileFlag, true);

        List<Map<String, Object>> mapList = fileUploadUtil.multiFileUpload(targetIdx, imageFiles, fileFlag);

        for (Map<String, Object> imageInfo : mapList) {
            String storedImagePath = (String)imageInfo.get("fileName");
            String originImagePath = (String)imageInfo.get("originalFileName");
            CreatePostImageDto createPostImageDto = new CreatePostImageDto(storedImagePath, originImagePath);

            list.add(createPostImageDto.toEntity());
        }

        return list;
    }

    public List<PostImageResponseDto> getImagesList(Long postIdx) {

        // 서버에서 이미지 이름에 postIdx가 포함되어 있는 이미지 찾기
        Map<String, String> storedImgsMap = findStoredImgs(postIdx);

        List<PostImage> imgList = postImageRepository.findByPostIdx(postIdx);
        if (imgList.isEmpty()) {
            throw new PostImageNotFoundException("해당 게시물에 이미지 정보가 존재하지 않습니다. postIdx : " + postIdx);
        }

        // DB에 저장된 이미지 정보와 서버에 저장된 이미지 정보가 일치하는지 확인
        return setValidImgList(imgList, storedImgsMap);
    }

    public String getFirstEncodingImage(Long postIdx) {

        PostImage topImg = postImageRepository.findTopImg(postIdx)
                .orElseThrow(() -> new PostImageNotFoundException("해당 게시물에 이미지 정보가 존재하지 않습니다. postIdx : " + postIdx));

        // TODO: 2024-06-20 예외처리 필요
        String uploadPath = fileUploadUtil.getUploadPath(ConstUtil.UPLOAD_IMAGE_FLAG);
        File file = new File(uploadPath, topImg.getStoreImageFile());

        return fileUploadUtil.getFileEncoding(file);
    }

    private Map<String, String> findStoredImgs(Long postIdx) {
        String uploadPath = fileUploadUtil.getUploadPath(ConstUtil.UPLOAD_IMAGE_FLAG);

        File path = new File(uploadPath);
        File[] fileList = path.listFiles();
        Map<String, String> matchesMap = new HashMap<>();

        if (fileList != null) {
            for (File file : fileList) {
                String fileName = file.getName();

                // 파일의 첫번째 문자가 postIdx가 아닐 경우, skip
                if (!fileName.startsWith(postIdx.toString())) {
                    continue;
                }

                // 해당 파일의 bytes를 읽어 base64로 인코딩 후 map에 저장
                String encodeFile = fileUploadUtil.getFileEncoding(file);
                matchesMap.put(fileName, encodeFile);
            }
        }
        return matchesMap;
    }

    private static List<PostImageResponseDto> setValidImgList(List<PostImage> imgList, Map<String, String> storedImgsMap) {
        List<PostImageResponseDto> matchesList = new ArrayList<>();
        long imgIdx = 0L;

        for (PostImage postImage : imgList) {
            if (storedImgsMap.containsKey(postImage.getStoreImageFile())) {

                matchesList.add(
                        PostImageResponseDto.builder()
                                .postImageIdx(++imgIdx)
                                .imageName(postImage.getUploadImageFile())
                                .encodingImage(storedImgsMap.get(postImage.getStoreImageFile()))
                                .build()
                );
            } else {
                throw new PostImageNotFoundException("해당 이미지를 서버에서 찾을 수 없습니다. name : "
                        + postImage.getStoreImageFile());
            }
        }
        return matchesList;
    }
}
