package mini.community.post.repository.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import mini.community.Profile.entity.Profile;
import mini.community.User.domain.User;
import mini.community.post.domain.Post;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GetPostResponseModel {
    private Post post;
    private User user;
    private Profile profile;

    public GetPostResponseModel(Post post, User user) {
        this.post = post;
        this.user = user;
    }
}
