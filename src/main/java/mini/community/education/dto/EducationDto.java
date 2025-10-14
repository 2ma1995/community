package mini.community.education.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import mini.community.education.domain.Education;

import java.time.LocalDate;

@Getter
@Schema(description = "학력 추가 요청 Dto")
public class EducationDto {
    @Schema(description = "학교")
    private String school;
    @Schema(description = "학년")
    private Integer degree;
    @Schema(description = "전공")
    private String major;
    @Schema(description = "학력 시간 날짜")
    private LocalDate startDate;
    @Schema(description = "학력 종료 날짜")
    private LocalDate endDate;
    @Schema(description = "현재 진행 여부")
    private boolean current;

    public Education toEntity() {
        return Education.builder()
                .school(school)
                .degree(degree)
                .major(major)
                .startDate(startDate)
                .endDate(current ? null : endDate)
                .build();
    }

}
