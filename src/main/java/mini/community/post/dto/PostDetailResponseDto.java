package mini.community.post.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import mini.community.post.repository.model.GetPostResponseModel;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@AllArgsConstructor
@Builder
public class PostDetailResponseDto {
    private Long id;
    private String text;
    private String name;
    private String avatar;
    private Long userId;
    private List<CommentResponseDto> comments;
    private LocalDateTime createdAt;

    public static PostDetailResponseDto of(GetPostResponseModel model, int likeCount, int disLikeCount, int commentCount) {
        return PostDetailResponseDto.builder()
                .id(model.getPost().getId())
                .text(model.getPost().getContents())
                .name(model.getUser().getName())
                .avatar(model.getProfile() != null ? model.getProfile().getImage() : null)
                .userId(model.getUser().getId())
                .comments(model.getPost().getComments().stream()
                        .map(CommentResponseDto::of)
                        .collect(Collectors.toList()))
                .createdAt(model.getPost().getCreatedAt())
                .build();
    }
}
