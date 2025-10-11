package mini.community.post.repository.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import mini.community.Profile.entity.Profile;
import mini.community.User.domain.User;
import mini.community.post.domain.Post;

@Getter
@Setter
@AllArgsConstructor
public class GetPostResponseModel {
    private User user;
    private Post post;
    private Profile profile;
}
