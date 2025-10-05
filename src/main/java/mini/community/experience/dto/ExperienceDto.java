package mini.community.experience.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import mini.community.experience.domain.Experience;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
public class ExperienceDto {
    private String title;

    private String company;

    private String position;

    private LocalDate from;

    private LocalDate to;

    private boolean current;

    private String description;

    public Experience toEntity() {
        return Experience.builder()
                .company(company)
                .position(position)
                .description(description)
                .startDate(from)
                .endDate(to)
                .build();
    }

}


