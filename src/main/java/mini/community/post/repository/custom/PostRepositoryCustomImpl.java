package mini.community.post.repository.custom;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import mini.community.post.repository.model.GetPostRequestModel;
import mini.community.post.repository.model.GetPostResponseModel;

import java.util.List;

import static mini.community.Profile.entity.QProfile.profile;
import static mini.community.User.domain.QUser.user;
import static mini.community.post.domain.QPost.post;


@RequiredArgsConstructor
public class PostRepositoryCustomImpl implements PostRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<GetPostResponseModel> getPosts(GetPostRequestModel model) {
        return getPostByModel(model)
                .orderBy(post.id.desc())
                .fetch();
    }

    @Override
    public GetPostResponseModel getPostById(GetPostRequestModel model) {
        return getPostByModel(model).fetchOne();
    }

    private JPAQuery<GetPostResponseModel> getPostByModel(GetPostRequestModel model) {
        return queryFactory
                .select(Projections.constructor(
                        GetPostResponseModel.class,
                        post,
                        user,
                        profile
                ))

                .from(post)
                .join(user).on(post.user.id.eq(user.id))
                .leftJoin(profile).on(profile.user.eq(user))
                .where(eqPostId(model.getPostId()));
    }

    private static BooleanExpression eqPostId(Long postId) {
        if (postId == null) return null;
        return post.id.eq(postId);
    }
}
