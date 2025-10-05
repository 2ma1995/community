package mini.community.dto;

import lombok.Getter;
import mini.community.Profile.entity.Profile;
import mini.community.User.entity.User;

import java.util.ArrayList;

@Getter
public class UpsertProfileDto {
    private String company;

    private String website;
    private String location;
    private String bio;
    private String status;
    private String githubUsername;
    private String image;

    public Profile toEntity(User user) {
        return Profile.builder()
                .user(user)
                .company(company)
                .website(website)
                .bio(bio)
                .status(status)
                .githubUsername(githubUsername)
                .image(image)
                .profileSkills(new ArrayList<>())
                .educations(new ArrayList<>())
                .experiences(new ArrayList<>())
                .build();
    }
}
