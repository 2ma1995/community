package mini.community.experience.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import mini.community.experience.domain.Experience;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
@Schema(description = "경력 추가 Dto")
public class ExperienceDto {
    @Schema(description = "직무/직책(타이틀)")
    private String title;
    @Schema(description = "회사")
    private String company;
    @Schema(description = "직무")
    private String position;
    @Schema(description = "경력 시작 날짜", example = "2024-01-01")
    private LocalDate from;
    @Schema(description = "경력 종료 날짜", example = "2024-12-31")
    private LocalDate to;
    @Schema(description = "현재 진행 여부", example = "false")
    private boolean current;
    @Schema(description = "경력 설명")
    private String description;

    public Experience toEntity() {
        return Experience.builder()
                .jobTitle(title)
                .company(company)
                .position(position)
                .startDate(from)
                .endDate(current ? null : to)
                .description(description)
                .build();
    }

}


