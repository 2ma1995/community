package mini.community.post.service;

import lombok.RequiredArgsConstructor;
import mini.community.Profile.repository.ProfileRepository;
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

    @Transactional
    public void createPost(Long userId, CreatePostDto dto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BadRequestException("유저를 찾을수 없습니다."));
        Post post = dto.toEntity(user);
        postRepository.save(post);
    }

    @Transactional(readOnly = true)
    public List<PostResponseDto> getPosts() {
        List<GetPostResponseModel> posts = postRepository.getPosts(GetPostRequestModel.builder().build());

        return posts.stream()
                .map(PostResponseDto::of)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public PostDetailResponseDto getPostById(Long postId) {
        GetPostResponseModel post = postRepository.getPostById(GetPostRequestModel.builder().postId(postId).build());
        return PostDetailResponseDto.of(post);
    }

    @Transactional
    public void deletePost(Long postId) {
        postRepository.deleteById(postId);
    }

    @Transactional
    public void likePost(Long userId, Long postId) {
        Post post = postRepository.findById(postId).orElseThrow(() -> new BadRequestException("Not Post"));
        boolean alreadyLike = post.getLikes()
                .stream()
                .map(like -> like.getUser().getId())
                .anyMatch(uid -> uid.equals(userId));

        if (alreadyLike) {
            throw new BadRequestException("이미 좋아요를 누른 게시물입니다.");
        }
        post.addLike(Like.builder()
                .user(userRepository.findById(userId).get())
                .build());
    }

    @Transactional
    public void unlikePost(Long userId, Long postId) {
        Post post = postRepository.findById(postId).orElseThrow(() -> new BadRequestException("Not Post"));
        boolean alreadyDisLike = post.getDislikes().stream().map(dislike -> dislike.getUser().getId()).anyMatch(uid -> uid.equals(userId));
        if (alreadyDisLike) {
            throw new BadRequestException("이미 싫어요를 누른 게시물입니다.");
        }
        post.addDislike(Dislike.builder()
                .user(userRepository.findById(userId).get())
                .build());
    }

    @Transactional
    public void cancelLikePost(Long userId, Long postId) {
        Post post = postRepository.findById(postId).orElseThrow(() -> new BadRequestException("Not Post"));
        Map<Long, Like> likes = post.getLikes().stream().collect(Collectors.toMap(like -> like.getUser().getId(), Function.identity()));
        if (!likes.containsKey(userId)) {
            throw new BadRequestException("좋아요 취소는 좋아요를 누른 게시물만 가능합니다.");
        }
        post.removeLike(likes.get(userId));
    }

    @Transactional
    public void cancelUnlikePost(Long userId, Long postId) {
        Post post = postRepository.findById(postId).orElseThrow(() -> new BadRequestException("Not Post"));
        Map<Long, Dislike> dislikes = post.getDislikes().stream().collect(Collectors.toMap(dislike -> dislike.getUser().getId(), Function.identity()));
        if (!dislikes.containsKey(userId)) {
            throw new BadRequestException("싫어요 취소는 싫어요를 누른 게시물만 가능합니다.");
        }
        post.removeDislike(dislikes.get(userId));
    }

    @Transactional
    public List<CommentResponseDto> addComment(Long userId, Long postId, CreateCommentDto commentDto) {
        Post post = postRepository.findById(postId).orElseThrow(() -> new BadRequestException("Not Post"));
        User user = userRepository.findById(userId).orElseThrow(() -> new BadRequestException("Not User"));

        post.addComment(commentDto.toEntity(user));
        return post.getComments().stream()
                .map(CommentResponseDto::of)
                .collect(Collectors.toList());
    }

    @Transactional
    public void removeComment(Long userId, Long postId, Long commentId) {
        Post post = postRepository.findById(postId).orElseThrow(() -> new BadRequestException("Not Post"));
        Map<Long, Comment> comments = post.getComments().stream().collect(Collectors.toMap(Comment::getId, Function.identity()));

        if (!comments.containsKey(commentId)) {
            throw new BadRequestException("댓글 삭제는 댓글을 단 게시물에만 가능합니다.");
        }
        post.removeComment(comments.get(commentId), userId);
    }


//    @Transactional
//    public PostResponseDto getPostResponse(Post post) {
//        User user = post.getUser();
//        Profile profile = profileRepository.findByUser(user)
//                .orElseThrow(() -> new BadRequestException("유저의 프로필이 없습니다."));
//        return PostResponseDto.builder()
//                .id(post.getId())
//                .contents(post.getContents())
//                .name(user.getUsername())
//                .avatar(profile.getImage())
//                .userId(user.getId())
//                .likeCount(post.getLikes().size())
//                .commentCount(post.getComments().size())
//                .createdAt(post.getCreatedAt())
//                .build();
//    }
}
