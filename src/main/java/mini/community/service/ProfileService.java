package mini.community.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import mini.community.domain.Profile;
import mini.community.domain.ProfileSkill;
import mini.community.domain.Skill;
import mini.community.dto.ProfileDto;
import mini.community.dto.UserDto;
import mini.community.repository.ProfileRepository;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProfileService {
    private final ProfileRepository profileRepository;

    @Transactional
    public List<ProfileDto> getProfiles() {
        List<Profile> profiles = profileRepository.findAll();
        List<ProfileDto> profileDtos = new ArrayList<>();
        for (Profile profile : profiles) {
            profileDtos.add(ProfileDto.builder()
                    .user(UserDto.from(profile.getUser()))
                    .bio(profile.getBio())
                    .company(profile.getCompany())
                    .location(profile.getLocation())
                    .githubUsername(profile.getGithubUsername())
                    .skills(profile.getProfileSkills().stream().map(ProfileSkill::getSkill).collect(Collectors.toList()))
                    .build());
        }
        return profileDtos;
    }
}
