package mini.community.Profile.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import mini.community.Profile.entity.Profile;
import mini.community.Profile.entity.ProfileSkill;
import mini.community.User.domain.User;
import mini.community.skill.domain.Skill;
import mini.community.education.dto.GetEducationDto;
import mini.community.experience.dto.GetExperienceDto;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
public class ProfileDetailDto {
    private Long id;
    @Schema(description = "유저 정보")
    private User user;
    @Schema(description = "회사명",example = "SK Hynix")
    private String company;
    @Schema(description = "웹사이트",example = "https://www.hynix.com")
    private String website;
    @Schema(description = "사는 곳",example = "Bucheon")
    private String location;
    @Schema(description = "깃허브 아이디",example = "hong123")
    private String githubUsername;
    @Schema(description = "자기소개", example = "안녕하세요")
    private String bio;
    @Schema(description = "유저 사진",example = "https://example.com/image.jpg")
    private String image;
    @Schema(description = "기술 스택")
    private List<Skill> skills;
    @Schema(description = "경력")
    private List<GetExperienceDto> experience;
    @Schema(description = "학력")
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
        this.skills = skills;
        this.experience = experience;
        this.education = education;
    }

    public static ProfileDetailDto fromEntity(Profile profile) {
        return ProfileDetailDto.builder()
                .user(profile.getUser())
                .company(profile.getCompany())
                .website(profile.getWebsite())
                .location(profile.getLocation())
                .bio(profile.getBio())
                .image(profile.getImage())
                .skills(profile.getProfileSkills().stream()
                        .map(ProfileSkill::getSkill)
                        .collect(Collectors.toList()))
                .experience(profile.getExperiences().stream().map(GetExperienceDto::fromEntity).collect(Collectors.toList()))
                .education(profile.getEducations().stream().map(GetEducationDto::fromEntity).collect(Collectors.toList()))
                .githubUsername(profile.getGithubUsername())
                .build();
    }

}
