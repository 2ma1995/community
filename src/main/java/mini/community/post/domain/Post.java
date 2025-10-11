package mini.community.post.domain;

import jakarta.persistence.*;
import lombok.*;
import mini.community.User.domain.User;
import mini.community.global.domain.BaseTimeEntity;
import mini.community.global.exception.BadRequestException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Table(name = "posts")
@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Post extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String contents;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Like> likes = new ArrayList<>();

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Dislike> dislikes = new ArrayList<>();

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comments = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = createdAt;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }


    public void addLike(Like like) {
        this.likes.add(like);
        if (like.getPost() != this) {
            like.setPost(this);
        }
    }

    public void removeLike(Like like) {
        Iterator<Like> iterator = this.likes.iterator();
        while (iterator.hasNext()) {
            Like e = iterator.next();
            if (like.equals(e)) {
                iterator.remove();
            }
        }
    }

    public void addDislike(Dislike dislike) {
        this.dislikes.add(dislike);
        if (dislike.getPost() != this) {
            dislike.setPost(this);
        }
    }

    public void removeDislike(Dislike dislike) {
        Iterator<Dislike> iterator = this.dislikes.iterator();
        while (iterator.hasNext()) {
            Dislike e = iterator.next();
            if (dislike.equals(e)) {
                iterator.remove();
            }
        }
    }

    public void addComment(Comment comment) {
        this.comments.add(comment);
        if (comment.getPost() != this) {
            comment.setPost(this);
        }
    }

    public void removeComment(Comment comment, Long userId) {
        if (comment.getUser().getId() != userId) {
            throw new BadRequestException("댓글 삭제는 댓글 작성자만 가능합니다.");
        }
        Iterator<Comment> iterator = this.comments.iterator();
        while (iterator.hasNext()) {
            Comment e = iterator.next();
            if (comment.equals(e)) {
                iterator.remove();
            }
        }
    }
}