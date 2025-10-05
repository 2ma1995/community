package mini.community.Profile.dto;

import lombok.*;
import mini.community.Profile.entity.Profile;

import java.util.List;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProfileListDto {
    private Long id;
    private String username;
    private String bio;
    private List<String> skills;

    public static ProfileListDto fromEntity(Profile profile) {
        return ProfileListDto.builder()
                .id(profile.getId())
                .username(profile.getUser().getUsername())
                .bio(profile.getBio())
                .skills(profile.getProfileSkills().stream().map(ps->ps.getSkill().getName()).toList()).build();
    }
}
