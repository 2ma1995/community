package mini.community.post.domain;

import jakarta.persistence.*;
import lombok.*;
import mini.community.User.domain.User;

import java.time.LocalDateTime;

@Entity
@Table(name = "dislikes", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"post_id", "user_id"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Dislike {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    public void setPost(Post post) {
        if (this.post != null) {
            this.post.getDislikes().remove(this);
        }
        this.post = post;
        if (!post.getDislikes().contains(this)) {
            post.getDislikes().add(this);
        }
    }
}
