package mini.community.dto;

import lombok.Builder;
import lombok.Getter;
import mini.community.domain.Education;

import java.time.LocalDateTime;

@Getter
@Builder
public class GetEducationDto {
    private Long id;
    private String school;
    private Integer degree;
    private String major;
    private LocalDateTime from;
    private LocalDateTime to;

    public GetEducationDto(Long id, String school, Integer degree, String major, LocalDateTime from, LocalDateTime to) {
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
