package mini.community.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
public class ProfileDto {
    private UserDto user;
    private String company;
    private String website;
    private String location;
    private String githubUsername;
    private String bio;
    private List<String> skills;

    @Builder
    public  ProfileDto(UserDto user, String company, String website, String location, String githubUsername, String bio, List<String> skills) {
        this.user = user;
        this.company = company;
        this.website = website;
        this.location = location;
        this.githubUsername = githubUsername;
        this.bio = bio;
        this.skills = skills;
    }
}
