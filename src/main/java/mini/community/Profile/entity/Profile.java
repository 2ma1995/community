package mini.community.Profile.entity;

import jakarta.persistence.*;
import lombok.*;
import mini.community.User.entity.User;
import mini.community.common.BaseTimeEntity;
import mini.community.domain.*;
import mini.community.dto.UpsertProfileDto;
import mini.community.education.domain.Education;
import mini.community.experience.domain.Experience;

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
    @Column(name = "id", updatable = false)
    private Long id;

    @OneToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // status가 ProfileStatus.code랑 매핑
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "status", referencedColumnName = "code", insertable = false, updatable = false)
    private ProfileStatus profileStatus;

    @Column(name = "status", nullable = false)
    private String status;

    @Column(name = "company")
    private String company;

    @Column(name = "website")
    private String website;

    @Column(name = "location")
    private String location;

    @Column(name = "githubUsername")
    private String githubUsername;

    @Column(name = "bio")
    private String bio;

    @Column(name = "image")
    private String image;

    @OneToMany(mappedBy = "profile", cascade = CascadeType.ALL)
    private List<Skill> skills = new ArrayList<>();


    public void changeSkills(List<Skill> skills) {
        this.profileSkills.clear();

        for (Skill skill : skills) {
            ProfileSkill profileSkill = new ProfileSkill();

            profileSkill.setProfile(this);
            profileSkill.setSkill(skill);

            this.profileSkills.add(profileSkill);
        }
    }

    @OneToMany(mappedBy = "profile", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Education> educations = new ArrayList<>();

    public void addEducation(Education education) {
        this.educations.add(education);
        if (education.getProfile() != null) {
            education.setProfile(this);
        }
    }


    @OneToMany(mappedBy = "profile", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Experience> experiences = new ArrayList<>();

    public void addExperience(Experience experience) {
        this.experiences.add(experience);
        if (experience.getProfile() != null) {
            experience.setProfile(this);
        }
    }

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



}
