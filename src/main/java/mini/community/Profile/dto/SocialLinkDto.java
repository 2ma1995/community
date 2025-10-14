package mini.community.Profile.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import mini.community.Profile.entity.SocialLink;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "sns Link")
public class SocialLinkDto {
    @Schema(description = "sns twitter")
    private String twitter;
    @Schema(description = "sns facebook")
    private String facebook;
    @Schema(description = "sns youtube")
    private String youtube;
    @Schema(description = "sns linkedin")
    private String linkedin;
    public static SocialLinkDto fromEntity(SocialLink socialLink) {
        return SocialLinkDto.builder()
                .twitter(socialLink.getTwitter())
                .facebook(socialLink.getFacebook())
                .youtube(socialLink.getYoutube())
                .linkedin(socialLink.getLinkedin())
                .build();
    }
}

