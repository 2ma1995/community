package mini.community.Profile.dto;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import mini.community.Profile.entity.Profile;
import mini.community.Profile.entity.ProfileSkill;
import mini.community.User.domain.User;
import mini.community.User.dto.UserSummaryDto;
import mini.community.skill.domain.Skill;
import mini.community.education.dto.GetEducationDto;
import mini.community.experience.dto.GetExperienceDto;
import mini.community.skill.dto.SkillDto;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProfileDetailDto {
    private Long id;
    @Schema(description = "유저 정보 요약")
    private UserSummaryDto user;
    @Schema(description = "회사명", example = "SK Hynix")
    private String company;
    @Schema(description = "웹사이트", example = "https://www.hynix.com")
    private String website;
    @Schema(description = "사는 곳", example = "Bucheon")
    private String location;
    @Schema(description = "깃허브 아이디", example = "hong123")
    private String githubUsername;
    @Schema(description = "자기소개", example = "안녕하세요")
    private String bio;
    @Schema(description = "유저 사진", example = "https://example.com/image.jpg")
    private String image;
    @ArraySchema(arraySchema = @Schema(description = "기술 스택"), schema = @Schema(implementation = SkillDto.class))
    private List<SkillDto> skills;
    @ArraySchema(arraySchema = @Schema(description = "경력"), schema = @Schema(implementation = GetExperienceDto.class))
    private List<GetExperienceDto> experience;
    @ArraySchema(arraySchema = @Schema(description = "교육"), schema = @Schema(implementation = GetEducationDto.class))
    private List<GetEducationDto> education;
    @ArraySchema(arraySchema = @Schema(description = "sns 링크"), schema = @Schema(implementation = SocialLinkDto.class))
    private List<SocialLinkDto> socialLinks;


    public static ProfileDetailDto fromEntity(Profile profile) {
        return ProfileDetailDto.builder()
                .id(profile.getId())
                .user(UserSummaryDto.fromEntity(profile.getUser()))
                .company(profile.getCompany())
                .website(profile.getWebsite())
                .location(profile.getLocation())
                .bio(profile.getBio())
                .image(profile.getImage())
                .skills(profile.getProfileSkills().stream()
                        .map(ProfileSkill::getSkill)
                        .map(SkillDto::fromEntity).toList())
                .experience(profile.getExperiences().stream().map(GetExperienceDto::fromEntity).toList())
                .education(profile.getEducations().stream().map(GetEducationDto::fromEntity).toList())
                .socialLinks(profile.getSocialLinks().stream().map(SocialLinkDto::fromEntity).toList())
                .githubUsername(profile.getGithubUsername())
                .build();
    }

}
