package mini.community.dto;

import lombok.*;
import mini.community.domain.Profile;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProfileDetailDto {
    private Long id;
    private UserDto user;
    private String company;
    private String website;
    private String location;
    private String githubUsername;
    private String bio;
    private String image;
    private List<String> skills;
    private List<GetExperienceDto> experience;
    private List<GetEducationDto> educations;


    public static ProfileDetailDto fromEntity(Profile profile) {
        return ProfileDetailDto.builder()
                .id(profile.getId())
                .user(UserDto.fromEntity(profile.getUser()))
                .company(profile.getCompany())
                .website(profile.getWebsite())
                .location(profile.getLocation())
                .githubUsername(profile.getGithubUsername())
                .bio(profile.getBio())
                .image(profile.getImage())
                .skills(profile.getProfileSkills().stream()
                        .map(ps -> ps.getSkill().getName())
                        .collect(Collectors.toList()))
                .experience(profile.getExperiences().stream().map(GetExperienceDto::from).collect(Collectors.toList()))
                .educations(profile.getEducations().stream().map(GetEducationDto::fromEntity).collect(Collectors.toList()))
                .build();
    }

}
