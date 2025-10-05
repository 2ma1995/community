package mini.community.Profile.dto;

import lombok.*;
import mini.community.User.entity.User;
import mini.community.domain.Skill;
import mini.community.education.domain.Education;
import mini.community.education.dto.GetEducationDto;
import mini.community.experience.domain.Experience;
import mini.community.experience.dto.GetExperienceDto;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
public class ProfileDetailDto {
    private Long id;
    private User user;
    private String company;
    private String website;
    private String location;
    private String githubUsername;
    private String bio;
    private String image;
    private List<String> skills;
    private List<GetExperienceDto> experience;
    private List<GetEducationDto> education;

    @Builder
    public ProfileDetailDto(User user, String company, String website, String location, String githubUsername, String bio, String image, List<Skill> skills, List<GetExperienceDto> experience, List<GetEducationDto> education) {
        this.user = user;
        this.company = company;
        this.website = website;
        this.location = location;
        this.githubUsername = githubUsername;
        this.bio = bio;
        this.image = image;
        this.skills = skills.stream().map(Skill::getName).collect(Collectors.toList());
        this.experience = experience;
        this.education = education;
    }

//    public static ProfileDetailDto fromEntity(Profile profile) {
//        return ProfileDetailDto.builder()
//                .id(profile.getId())
//                .user(profile.getUser())
//                .company(profile.getCompany())
//                .website(profile.getWebsite())
//                .location(profile.getLocation())
//                .githubUsername(profile.getGithubUsername())
//                .bio(profile.getBio())
//                .image(profile.getImage())
//                .skills(profile.getProfileSkills().stream()
//                        .map(ps -> ps.getSkill().getName())
//                        .collect(Collectors.toList()))
//                .experience(profile.getExperiences().stream().map(GetExperienceDto::from).collect(Collectors.toList()))
//                .educations(profile.getEducations().stream().map(GetEducationDto::fromEntity).collect(Collectors.toList()))
//                .build();
//    }

}
