package mini.community.experience.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import mini.community.experience.domain.Experience;

import java.time.LocalDate;

@Getter
@Builder
@AllArgsConstructor
public class GetExperienceDto {
    private Long id;
    private String company;
    private String position;
    private String description;
    private LocalDate from;
    private LocalDate to;

    public static GetExperienceDto from(Experience experience) {
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
