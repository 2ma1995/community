package mini.community.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "profiles")
@Getter
@Setter
@NoArgsConstructor
public class Profile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // status가 ProfileStatus.code랑 매핑
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "status", referencedColumnName = "code", insertable = false, updatable = false)
    private ProfileStatus profileStatus;

    @Column(name = "status", nullable = false)
    private String status;

    private String company;

    private String website;

    private String location;

    @Column(name = "githubUsername")
    private String githubUsername;

    @Column(columnDefinition = "TEXT")
    private String bio;

    private String image;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "profile", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Education> educations = new ArrayList<>();

    @OneToMany(mappedBy = "profile", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Experience> experiences = new ArrayList<>();

    @OneToMany(mappedBy = "profile", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SocialLink> socialLinks = new ArrayList<>();

    @OneToMany(mappedBy = "profile", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProfileSkill> profileSkills = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = createdAt;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    @Builder
    public Profile(Long id, User user, ProfileStatus profileStatus,String status,String company,String website,String location,String githubUsername,String bio,String image) {
        this.id = id;
        this.user = user;
        this.profileStatus = profileStatus;
        this.status = status;
        this.company = company;
        this.website = website;
        this.location = location;
        this.githubUsername = githubUsername;
        this.bio = bio;
        this.image = image;
    }
}
