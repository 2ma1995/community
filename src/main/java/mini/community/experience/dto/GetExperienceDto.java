package mini.community.experience.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import mini.community.experience.domain.Experience;

import java.time.LocalDate;

@Getter
@Builder
@AllArgsConstructor
@Schema(description = "경력조회 DTO")
public class GetExperienceDto {
    @Schema(description = "경력 id")
    private Long id;
    @Schema(description = "회사")
    private String company;
    @Schema(description = "직무")
    private String position;
    @Schema(description = "직무 설명")
    private String description;
    @Schema(description = "경력 시작 날짜")
    private LocalDate from;
    @Schema(description = "경력 종료 날짜")
    private LocalDate to;

    public static GetExperienceDto fromEntity(Experience experience) {
        return GetExperienceDto.builder()
                .id(experience.getId())
                .company(experience.getCompany())
                .position(experience.getPosition())
                .description(experience.getDescription())
                .from(experience.getStartDate())
                .to(experience.getEndDate())
                .build();
    }
}
