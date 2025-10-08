package mini.community.Profile.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SocialLinkDto {
    private String twitter;
    private String facebook;
    private String youtube;
    private String linkedin;
}
