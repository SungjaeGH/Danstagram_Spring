package com.project.danstagram.domain.post.service;

import com.project.danstagram.domain.comment.repository.CommentRepository;
import com.project.danstagram.domain.member.entity.Member;
import com.project.danstagram.domain.member.repository.MemberRepository;
import com.project.danstagram.domain.post.dto.*;
import com.project.danstagram.domain.post.entity.Post;
import com.project.danstagram.domain.post.entity.PostImage;
import com.project.danstagram.domain.post.exception.PostNotFoundException;
import com.project.danstagram.domain.post.repository.PostRepository;
import com.project.danstagram.domain.post.repository.PostRepositoryCustom;
import com.project.danstagram.global.scroll.PageRequestUtil;
import com.project.danstagram.global.scroll.ScrollPaginationCollection;
import com.project.danstagram.global.time.TimeFormat;
import com.project.danstagram.global.time.TimeUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PostService {

    private final PostRepository postRepository;
    private final PostRepositoryCustom postRepositoryCustom;
    private final PostImageService postImageService;
    private final MemberRepository memberRepository;
    private final CommentRepository commentRepository;
    private final TimeUtil timeUtil;

    @Transactional
    public PostResponse.CreatePost createPost(PostRequest.CreatePost request, List<MultipartFile> imageFiles) throws IOException {

        Post savedPost = request.toEntity(timeUtil.getCurrTime(TimeFormat.TimeFormat1));

        // 회원 존재 유무 확인 및 Member 엔티티의 post list에 저장
        Member member = memberRepository.findByMemberId(request.writerId())
                .orElseThrow(() -> new UsernameNotFoundException("해당하는 회원을 찾을 수 없습니다. id : " + request.writerId()));
        member.putPost(savedPost);

        Post saved = postRepository.save(savedPost);

        // Multipart로 받은 이미지 파일 정보들을 PostImage Entity List로 세팅
        List<PostImage> postImages = postImageService.setImageList(saved.getPostIdx(), imageFiles);

        // Post Entity에 이미지 파일 정보들 저장
        postImages.forEach(saved::putPostImage);
        postRepository.save(saved);

        return PostResponse.CreatePost.builder()
                .postIdx(saved.getPostIdx())
                .build();
    }

    @Transactional
    public PostResponse.FindPost findPost(Long postIdx) {

        Post findPost = postRepository.findById(postIdx)
                .orElseThrow(() -> new PostNotFoundException("해당 게시글을 찾을 수 없습니다. Idx: " + postIdx));

        return PostResponse.FindPost.builder()
                .postContent(findPost.getPostContent())
                .postDate(findPost.getPostDate())
                .postUpdateDate(findPost.getPostUpdateDate())
                .postDeleteDate(findPost.getPostDeleteDate())
                .postImageList(postImageService.getImagesList(postIdx))
                .build();
    }

    @Transactional
    public PostResponse.UpdatePost updatePost(Long postIdx, PostRequest.UpdatePost request) {

        Post updatedPost = postRepository.findById(postIdx)
                .orElseThrow(() -> new PostNotFoundException("해당 게시글을 찾을 수 없습니다. Idx: " + postIdx));

        Post saved = postRepository.save(request.toEntity(timeUtil.getCurrTime(TimeFormat.TimeFormat1), updatedPost));

        return PostResponse.UpdatePost.builder()
                .postIdx(saved.getPostIdx())
                .isPostUpdate(true)
                .postUpdateDate(saved.getPostUpdateDate())
                .build();
    }

    @Transactional
    public PostResponse.PostListForProfile findPostForProfile(PostRequest.FindPostForProfile request) {

        PageRequest pageRequest = PageRequestUtil.setPageRequest(request.scrollSize());
        List<PostResponse.PostInfoForProfile> postInfos =
                postRepositoryCustom.findPostForProfile(request.memberId(), request.lastPostIdx(), pageRequest);

        Long nextCursor = -1L;
        ScrollPaginationCollection<PostResponse.PostInfoForProfile> cursor =
                ScrollPaginationCollection.of(postInfos, request.scrollSize());
        if (!cursor.isLastScroll()) {

            nextCursor = cursor.getNextCursor().getPostIdx();
        }

        List<PostResponse.PostInfoForProfile> currentScrollItems = cursor.getCurrentScrollItems();

        return PostResponse.PostListForProfile.builder()
                    .totalElements(postRepositoryCustom.countTotalPosts(request.memberId()))
                    .nextCursor(nextCursor)
                    .contents(appendPostsInfo(currentScrollItems))
                .build();
    }

    private List<PostResponse.PostInfoForProfile> appendPostsInfo(List<PostResponse.PostInfoForProfile> postList) {

        return postList.stream().map(info ->
                PostResponse.PostInfoForProfile.builder()
                            .postIdx(info.getPostIdx())
                            .postLikeCount(info.getPostLikeCount())
                            .postImg(postImageService.getFirstEncodingImage(info.getPostIdx()))
                            .postCommentCount(commentRepository.countCommentsByPostIdx(info.getPostIdx()))
                        .build())
                .collect(Collectors.toList());
    }

    @Transactional
    public PostResponse.UpdatePostDeleteStatus updatePostDeleteStatus(PostRequest.UpdatePostDeleteStatus request) {

        Post post = postRepository.findById(request.getPostIdx())
                .orElseThrow(() -> new PostNotFoundException("해당 게시글을 찾을 수 없습니다. Idx: " + request.getPostIdx()));

        String deleteDate = null;
        if (request.isPostDelete()) {
            deleteDate = timeUtil.getCurrTime(TimeFormat.TimeFormat1);
        }

        Post saved = postRepository.save(request.toEntity(post, deleteDate));

        return PostResponse.UpdatePostDeleteStatus.builder()
                    .postIdx(saved.getPostIdx())
                    .isPostDelete(request.isPostDelete())
                    .postDeleteDate(saved.getPostDeleteDate())
                .build();
    }
}