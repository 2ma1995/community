package mini.community.education.dto;

import lombok.Builder;
import lombok.Getter;
import mini.community.education.domain.Education;

import java.time.LocalDate;

@Getter
@Builder
public class GetEducationDto {
    private Long id;
    private String school;
    private Integer degree;
    private String major;
    private LocalDate from;
    private LocalDate to;

    public GetEducationDto(Long id, String school, Integer degree, String major, LocalDate from, LocalDate to) {
        this.id = id;
        this.school = school;
        this.degree = degree;
        this.major = major;
        this.from = from;
        this.to = to;
    }

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
