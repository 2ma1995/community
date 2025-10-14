package mini.community.Profile.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import mini.community.Profile.entity.Profile;

import java.util.List;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "프로필 목록 조회")
public class ProfileListDto {
    @Schema(description = "프로필목록 ID")
    private Long id;
    @Schema(description = "유저 이름")
    private String username;
    @Schema(description = "유저 지역 정보")
    private String location;
    @Schema(description = "유저 자기소개")
    private String bio;
    @Schema(description = "유저 기술 스택")
    private List<String> skills;

    public static ProfileListDto fromEntity(Profile profile) {
        return ProfileListDto.builder()
                .id(profile.getId())
                .username(profile.getUser().getName())
                .location(profile.getLocation())
                .bio(profile.getBio())
                .skills(profile.getProfileSkills().stream().map(ps->ps.getSkill().getName()).toList()).build();
    }
}
