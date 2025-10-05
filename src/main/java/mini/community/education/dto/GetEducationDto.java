package mini.community.education.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import mini.community.education.domain.Education;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
@Builder
public class GetEducationDto {
    private Long id;
    private String school;
    private Integer degree;
    private String major;
    private LocalDate from;
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
