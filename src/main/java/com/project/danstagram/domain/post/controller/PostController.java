package com.project.danstagram.domain.post.controller;

import com.project.danstagram.domain.post.dto.CreatePostDto;
import com.project.danstagram.domain.post.dto.PostResponseDto;
import com.project.danstagram.domain.post.dto.UpdatePostDto;
import com.project.danstagram.domain.post.service.PostImageService;
import com.project.danstagram.domain.post.service.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/p")
public class PostController {

    private final PostService postService;
    private final PostImageService postImageService;

    @PostMapping("/create")
    public ResponseEntity<PostResponseDto> createPost(@RequestPart("body") CreatePostDto createPostDto,
                                                      @RequestPart("images") List<MultipartFile> imageFiles) throws IOException {

        // post 저장 & postImage 저장
        PostResponseDto createResult = postService.createPost(createPostDto, imageFiles);
        if (createResult.getPostIdx() == 0) {
            ResponseEntity.internalServerError().build();
        }

        return ResponseEntity.ok(createResult);
    }

    @GetMapping("/find/{postIdx}")
    public ResponseEntity<PostResponseDto> findPost(@PathVariable Long postIdx) {
        return ResponseEntity.ok(postService.findPost(postIdx));
    }

    @GetMapping("/display/images/{postIdx}")
    public ResponseEntity<Map<String, String>> getPostImage(@PathVariable Long postIdx) {

        Map<String, String> imagesMap = postImageService.getImagesList(postIdx);
        return ResponseEntity.ok()
                .body(imagesMap);
    }

    @PatchMapping("/update/{postIdx}")
    public ResponseEntity<PostResponseDto> updatePost(@PathVariable Long postIdx,
                                                      @RequestBody UpdatePostDto updatePostDto) {

        return ResponseEntity.ok(postService.updatePost(postIdx, updatePostDto));
    }
}
