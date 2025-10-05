package mini.community.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import mini.community.Profile.entity.Profile;

@Entity
@Table(name = "social_links")
@Getter
@Setter
@NoArgsConstructor
public class SocialLink {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "profile_id", nullable = false)
    private Profile profile;

    private String twitter;
    private String facebook;
    private String youtube;
    private String linkedin;
}
