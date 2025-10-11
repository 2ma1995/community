package mini.community.post.dto;

import lombok.Getter;
import mini.community.User.domain.User;
import mini.community.post.domain.Post;

import java.util.ArrayList;

@Getter
public class CreatePostDto {
    private String text;

    public Post toEntity(User user) {
        return Post.builder()
                .user(user)
                .contents(text)
                .likes(new ArrayList<>())
                .dislikes(new ArrayList<>())
                .comments(new ArrayList<>())
                .build();
    }
}
