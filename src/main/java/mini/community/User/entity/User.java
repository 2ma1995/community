package mini.community.User.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import mini.community.common.BaseTimeEntity;

@Getter
@Entity
@NoArgsConstructor
@Table(name = "User")
public class User extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Builder
    public User(String name, String email, String password) {
        this.username = name;
        this.email = email;
        this.password = password;
    }

}
