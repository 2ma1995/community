package mini.community.Profile.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class UpsertProfileDto {
    private String status;
    private String company;
    private String website;
    private String location;
    private String image;
    private List<String> skills;
    private String githubUsername;
    private String bio;
    private List<SocialLinkDto> socialLinks;
}
