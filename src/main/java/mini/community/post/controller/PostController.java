package mini.community.post.controller;

import lombok.RequiredArgsConstructor;
import mini.community.global.context.TokenContext;
import mini.community.global.context.TokenContextHolder;
import mini.community.post.dto.*;
import mini.community.post.service.PostService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/posts")
public class PostController {
    private final PostService postService;

    @PostMapping
    public ResponseEntity<?> createPost(@RequestBody CreatePostDto dto) {
        long userId = TokenContextHolder.getContext().getUserId();
        postService.createPost(userId, dto);
        return ResponseEntity.status(HttpStatus.CREATED).body("게시글 생성 완료");
    }

    @GetMapping
    public List<PostResponseDto> getPosts() {
        return postService.getPosts();
    }

    @GetMapping("/{post_id}")
    public PostDetailResponseDto getPostById(@PathVariable("post_id") long postId) {
        return postService.getPostById(postId);
    }

    @DeleteMapping("/{post_id}")
    public ResponseEntity<?> deletePost(@PathVariable("post_id") long postId) {
        postService.deletePost(postId);
        return ResponseEntity.status(HttpStatus.OK).body("게시글이 삭제되었습니다.");
    }

    @PutMapping("/like/{post-id}")
    public void likePost(@PathVariable("post-id") long postId) {
        TokenContext context = TokenContextHolder.getContext();
        long userId = context.getUserId();
        postService.likePost(userId, postId);
    }

    @PutMapping("/disLike/{post-id}")
    public void disLikePost(@PathVariable("post-id") long postId) {
        TokenContext context = TokenContextHolder.getContext();
        long userId = context.getUserId();
        postService.unlikePost(userId, postId);
    }

    @PutMapping("/cancelLike/{post-id}")
    public void cancelLikePost(@PathVariable("post-id") long postId) {
        TokenContext context = TokenContextHolder.getContext();
        long userId = context.getUserId();
        postService.cancelLikePost(userId, postId);
    }

    @PutMapping("/cancelUnLike/{post-id}")
    public void cancelDisLikePost(@PathVariable("post-id") long postId) {
        TokenContext context = TokenContextHolder.getContext();
        long userId = context.getUserId();
        postService.cancelUnlikePost(userId, postId);
    }


    @PostMapping("/comment/{post-id}")
    public List<CommentResponseDto> addComment(@PathVariable("post-id") long postId, @RequestBody CreateCommentDto commonDto) {
        TokenContext context = TokenContextHolder.getContext();
        long userId = context.getUserId();
        return postService.addComment(userId, postId, commonDto);
    }

    @DeleteMapping("/comment/{post-id}/{comment-id}")
    public void removeComment(@PathVariable("post-id") long postId,@PathVariable("comment-id") long commentId) {
        TokenContext context = TokenContextHolder.getContext();
        long userId = context.getUserId();
        postService.removeComment(userId, postId, commentId);
    }
}
