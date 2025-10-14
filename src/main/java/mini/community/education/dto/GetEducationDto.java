package mini.community.education.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import mini.community.education.domain.Education;

import java.time.LocalDate;

@Getter
@Builder
@Schema(description = "학력 조회 Dto")
public class GetEducationDto {
    @Schema(description = "학력 id")
    private Long id;
    @Schema(description = "학교")
    private String school;
    @Schema(description = "학년")
    private Integer degree;
    @Schema(description = "전공")
    private String major;
    @Schema(description = "학력 시작 날짜")
    private LocalDate from;
    @Schema(description = "학력 종료 날짜")
    private LocalDate to;

    public static GetEducationDto fromEntity(Education education) {
        return GetEducationDto.builder()
                .id(education.getId())
                .school(education.getSchool())
                .degree(education.getDegree())
                .major(education.getMajor())
                .from(education.getStartDate())
                .to(education.getEndDate())
                .build();
    }
}
