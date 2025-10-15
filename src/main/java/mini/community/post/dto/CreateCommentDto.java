package mini.community.post.dto;

import lombok.Getter;
import mini.community.User.domain.User;
import mini.community.post.domain.Comment;

@Getter
public class CreateCommentDto {
    private String contents;

    public Comment toEntity(User writer) {
        return Comment.builder()
                .user(writer)
                .contents(contents)
                .build();
    }
}
