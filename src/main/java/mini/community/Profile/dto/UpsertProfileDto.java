package mini.community.Profile.dto;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import mini.community.skill.dto.SkillDto;

import java.util.List;

@Getter
@Setter
@Schema(description = "프로필 생성 및 수정 요청 DTO")
public class UpsertProfileDto {
    @Schema(description = "회사")
    private String company;
    @Schema(description = "개인 웹사이트")
    private String website;
    @Schema(description = "사는 지역")
    private String location;
    @Schema(description = "https://example.com/img.jpg")
    private String image;
    @ArraySchema(arraySchema = @Schema(description = "기술 스택"), schema = @Schema(implementation = SkillDto.class))
    private List<String> skills;
    @Schema(description = "현재 구직 상태")
    private String status;
    @Schema(description = "깃허브 아이디")
    private String githubUsername;
    @Schema(description = "자기 소개")
    private String bio;
    @ArraySchema(arraySchema = @Schema(description = "sns 링크"), schema = @Schema(implementation = SocialLinkDto.class))
    private List<SocialLinkDto> socialLinks;
}
