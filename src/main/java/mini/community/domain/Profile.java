package mini.community.domain;

import jakarta.persistence.*;
import lombok.*;
import mini.community.dto.UpsertProfileDto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "profiles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Profile extends BaseTimeEntity {
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

    @OneToMany(mappedBy = "profile", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Education> educations = new ArrayList<>();

    @OneToMany(mappedBy = "profile", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Experience> experiences = new ArrayList<>();

    @OneToMany(mappedBy = "profile", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SocialLink> socialLinks = new ArrayList<>();

    @OneToMany(mappedBy = "profile", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProfileSkill> profileSkills = new ArrayList<>();

    public void update(UpsertProfileDto profileDto) {
        if (profileDto.getStatus() != null) {
            this.status = profileDto.getStatus();
        }
        if (profileDto.getCompany() != null) {this.company = profileDto.getCompany();}
        if (profileDto.getWebsite() != null) {this.website = profileDto.getWebsite();}
        if (profileDto.getLocation() != null) {this.location = profileDto.getLocation();}
        if (profileDto.getBio() != null) {this.bio = profileDto.getBio();}
        if (profileDto.getImage() != null) {this.image = profileDto.getImage();}
        if (profileDto.getStatus() != null) {this.status = profileDto.getStatus();}
        if (profileDto.getGithubUsername() != null) {this.githubUsername = profileDto.getGithubUsername();}
    }

    public void changeSkills(List<Skill> skills) {
        this.skills.clear();

        for (Skill skill : skills) {
            this.addSkill(skill);
        }
    }

    public void addExperience(Experience experience) {
        this.experiences.add(experience);
        if (experience.getProfile() != null) {
            experience.setProfile(this);
        }
    }
}
