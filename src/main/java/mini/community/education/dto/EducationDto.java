package mini.community.education.dto;

import lombok.Getter;
import mini.community.education.domain.Education;

import java.time.LocalDate;

@Getter
public class EducationDto {
    private String school;
    private Integer degree;
    private String major;
    private LocalDate startDate;
    private LocalDate endDate;
    private boolean current;

    public Education toEntity() {
        return Education.builder()
                .school(school)
                .degree(degree)
                .major(major)
                .startDate(startDate)
                .endDate(endDate)
                .build();
    }

}
