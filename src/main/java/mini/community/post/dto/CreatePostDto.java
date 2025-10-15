package mini.community.post.dto;

import lombok.Getter;
import mini.community.User.domain.User;
import mini.community.post.domain.Post;

import java.util.ArrayList;

@Getter
public class CreatePostDto {
    private String contents;

    public Post toEntity(User user) {
        return Post.builder()
                .user(user)
                .contents(contents)
                .likes(new ArrayList<>())
                .dislikes(new ArrayList<>())
                .comments(new ArrayList<>())
                .build();
    }
}
