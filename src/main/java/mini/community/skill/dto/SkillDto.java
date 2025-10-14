package mini.community.skill.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import mini.community.skill.domain.Skill;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "기술 Dto")
public class SkillDto {
    @Schema(description = "스킬 ID",example = "1")
    private Long id;
    @Schema(description = "스킬 이름", example = "Java")
    private String name;

    public static SkillDto fromEntity(Skill skill) {
        return new SkillDto(skill.getId(), skill.getName());
    }
}
