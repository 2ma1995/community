package mini.community.post.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import mini.community.Profile.entity.Profile;
import mini.community.post.domain.Comment;

@Getter
@AllArgsConstructor
@Builder
public class CommentResponseDto {
    private Long id;
    private String content;
    private String name;
    private String avatar;
    private Long userId;

    public static CommentResponseDto of(Comment comment) {
        Profile profile = comment.getUser().getProfile();
        return CommentResponseDto.builder()
                .id(comment.getId())
                .content(comment.getContents())
                .name(comment.getUser().getUsername())
                .avatar(profile!=null?profile.getImage():null)
                .userId(comment.getUser().getId())
                .build();
    }
}
