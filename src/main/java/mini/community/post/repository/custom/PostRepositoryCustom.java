package mini.community.post.repository.custom;

import mini.community.post.repository.model.GetPostRequestModel;
import mini.community.post.repository.model.GetPostResponseModel;

import java.util.List;

public interface PostRepositoryCustom {
    List<GetPostResponseModel> getPosts(GetPostRequestModel model);
    GetPostResponseModel getPostById(GetPostRequestModel model);
}
