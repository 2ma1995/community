package mini.community.post.service;

import lombok.RequiredArgsConstructor;
import mini.community.User.domain.User;
import mini.community.User.repository.UserRepository;
import mini.community.global.exception.BadRequestException;
import mini.community.post.domain.Comment;
import mini.community.post.domain.Dislike;
import mini.community.post.domain.Like;
import mini.community.post.domain.Post;
import mini.community.post.dto.*;
import mini.community.post.repository.model.GetPostRequestModel;
import mini.community.post.repository.model.GetPostResponseModel;
import mini.community.post.repository.PostRepository;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final RedisTemplate<String ,String> redisTemplate;

    private static final String LIKE_KEY = "post:likes";
    private static final String DISLIKE_KEY = "post:dislikes";
    private static final String COMMENT_KEY = "post:comments";

    @Transactional
    public void createPost(Long userId, CreatePostDto dto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BadRequestException("유저를 찾을수 없습니다."));
        Post post = dto.toEntity(user);
        postRepository.save(post);

        // redis 초기화 (좋아요:0)
        if (redisTemplate.opsForZSet().score(LIKE_KEY, post.getId().toString()) == null){
            redisTemplate.opsForZSet().add(LIKE_KEY, post.getId().toString(), 0);
        }
        if (redisTemplate.opsForZSet().score(DISLIKE_KEY, post.getId().toString()) == null){
            redisTemplate.opsForZSet().add(DISLIKE_KEY, post.getId().toString(), 0);
        }
        if (redisTemplate.opsForZSet().score(COMMENT_KEY, post.getId().toString()) == null){
            redisTemplate.opsForZSet().add(COMMENT_KEY, post.getId().toString(), 0);
        }

    }

    // 게시글 전체 조회
    @Transactional(readOnly = true)
    public List<PostResponseDto> getPosts() {
        List<GetPostResponseModel> posts = postRepository.getPosts(GetPostRequestModel.builder().build());

        return posts.stream()
                .map(model -> PostResponseDto.of(
                        model,
                        getLikeCount(model.getPost().getId()),
                        getDislikeCount(model.getPost().getId()),
                        getCommentCount(model.getPost().getId())))
                .collect(Collectors.toList());
    }

    //게시글 상세 조회 (redis 좋아요 반영)
    @Transactional(readOnly = true)
    public PostDetailResponseDto getPostById(Long postId) {
        GetPostResponseModel post = postRepository.getPostById(
                GetPostRequestModel.builder().postId(postId).build());
        return PostDetailResponseDto.of(
                post,
                getLikeCount(postId),
                getDislikeCount(postId),
                getCommentCount(postId)
                );
    }

    //게시글 삭제
    @Transactional
    public void deletePost(Long postId) {
        postRepository.deleteById(postId);
        redisTemplate.opsForZSet().remove(LIKE_KEY, postId.toString());
        redisTemplate.opsForZSet().remove(DISLIKE_KEY, postId.toString());
        redisTemplate.opsForZSet().remove(COMMENT_KEY, postId.toString());
    }

    // 좋아요
    @Transactional
    public void likePost(Long userId, Long postId) {
        Post post = findPost(postId);
        boolean alreadyLike = post.getLikes()
                .stream()
                .map(like -> like.getUser().getId())
                .anyMatch(uid -> uid.equals(userId));
        if (alreadyLike) {
            throw new BadRequestException("이미 좋아요를 누른 게시물입니다.");
        }
        post.addLike(Like.builder()
                .user(findUser(userId))
                .build());
        // redis 좋아요 +1
        redisTemplate.opsForZSet().incrementScore(LIKE_KEY, post.getId().toString(), 1);

    }

    // 좋아요 취소
    @Transactional
    public void cancelLikePost(Long userId, Long postId) {
        Post post = findPost(postId);
        Map<Long, Like> likes = post.getLikes().stream().collect(Collectors.toMap(like -> like.getUser().getId(), Function.identity()));
        if (!likes.containsKey(userId)) {
            throw new BadRequestException("좋아요 취소는 좋아요를 누른 게시물만 가능합니다.");
        }
        post.removeLike(likes.get(userId));
        // redis 좋아요 -1
        redisTemplate.opsForZSet().incrementScore(LIKE_KEY, post.getId().toString(), -1);
    }

    // 싫어요
    @Transactional
    public void unlikePost(Long userId, Long postId) {
        Post post = findPost(postId);
        boolean alreadyDisLike = post.getDislikes().stream().map(dislike -> dislike.getUser().getId()).anyMatch(uid -> uid.equals(userId));
        if (alreadyDisLike) {
            throw new BadRequestException("이미 싫어요를 누른 게시물입니다.");
        }
        post.addDislike(Dislike.builder()
                .user(findUser(userId))
                .build());
        redisTemplate.opsForZSet().incrementScore(DISLIKE_KEY, post.getId().toString(), 1);
    }

    @Transactional
    public void cancelUnlikePost(Long userId, Long postId) {
        Post post = findPost(postId);
        Map<Long, Dislike> dislikes = post.getDislikes().stream().collect(Collectors.toMap(dislike -> dislike.getUser().getId(), Function.identity()));
        if (!dislikes.containsKey(userId)) {
            throw new BadRequestException("싫어요 취소는 싫어요를 누른 게시물만 가능합니다.");
        }
        post.removeDislike(dislikes.get(userId));
        redisTemplate.opsForZSet().incrementScore(DISLIKE_KEY, post.getId().toString(), -1);
    }

    @Transactional
    public List<CommentResponseDto> addComment(Long userId, Long postId, CreateCommentDto commentDto) {
        Post post = findPost(postId);
        User user = findUser(userId);

        post.addComment(commentDto.toEntity(user));

        redisTemplate.opsForZSet().incrementScore(COMMENT_KEY, post.getId().toString(), 1);

        return post.getComments().stream()
                .map(CommentResponseDto::of)
                .collect(Collectors.toList());
    }

    @Transactional
    public void removeComment(Long userId, Long postId, Long commentId) {
        Post post = findPost(postId);
        Map<Long, Comment> comments = post.getComments().stream().collect(Collectors.toMap(Comment::getId, Function.identity()));

        if (!comments.containsKey(commentId)) {
            throw new BadRequestException("댓글 삭제는 댓글을 단 게시물에만 가능합니다.");
        }
        post.removeComment(comments.get(commentId), userId);
        redisTemplate.opsForZSet().incrementScore(COMMENT_KEY, post.getId().toString(), -1);
    }

    // 공통 유틸
    private Post findPost(Long postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new BadRequestException("게시글을 찾을수 없습니다."));
    }

    private User findUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new BadRequestException("유저를 찾을수 없습니다."));
    }

    //  Redis 개수 조회 유틸
    private int getLikeCount(Long postId) {
        return getCountFromRedis(LIKE_KEY, postId);
    }

    private int getDislikeCount(Long postId) {
        return getCountFromRedis(DISLIKE_KEY, postId);
    }

    private int getCommentCount(Long postId) {
       return getCountFromRedis(COMMENT_KEY, postId);
    }

    private int getCountFromRedis(String key, Long postId) {
        Double score = redisTemplate.opsForZSet().score(key, postId.toString());
        return (score != null) ? score.intValue() : 0;
    }

}
