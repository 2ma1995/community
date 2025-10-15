package mini.community.post.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import mini.community.post.domain.Post;
import mini.community.post.repository.model.GetPostResponseModel;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@Builder
public class PostResponseDto {
    private Long id;
    private String contents;
    private String name;
    private String avatar;
    private Long userId;
    private Integer likeCount;
    private Integer disLikeCount;
    private Integer commentCount;
    private LocalDateTime createdAt;

    public static PostResponseDto of(GetPostResponseModel model) {
        return PostResponseDto.builder()
                .id(model.getPost().getId())
                .contents(model.getPost().getContents())
                .name(model.getUser().getName())
                .avatar(model.getProfile() != null ? model.getProfile().getImage() : null)
                .userId(model.getUser().getId())
                .likeCount(model.getPost().getLikes().size())
                .disLikeCount(model.getPost().getDislikes().size())
                .createdAt(model.getPost().getCreatedAt())
                .build();
    }
}
