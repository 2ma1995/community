package mini.community.post.repository.model;

import lombok.Builder;
import lombok.Getter;

@Getter
public class GetPostRequestModel {
    private Long postId;

    @Builder
    public GetPostRequestModel(Long postId) {
        this.postId = postId;
    }
}
