package mini.community.Profile.service;

import lombok.RequiredArgsConstructor;
import mini.community.Profile.entity.Profile;
import mini.community.User.entity.User;
import mini.community.domain.Skill;
import mini.community.education.dto.GetEducationDto;
import mini.community.experience.dto.ExperienceDto;
import mini.community.Profile.dto.ProfileDetailDto;
import mini.community.Profile.dto.ProfileListDto;
import mini.community.dto.UpsertProfileDto;
import mini.community.education.dto.EducationDto;
import mini.community.experience.dto.GetExperienceDto;
import mini.community.global.exception.BadRequestException;
import mini.community.education.repository.EducationRepository;
import mini.community.experience.repository.ExperienceRepository;
import mini.community.Profile.repository.ProfileRepository;
import mini.community.User.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProfileService {
    private final UserRepository userRepository;
    private final ProfileRepository profileRepository;
    private final ExperienceRepository experienceRepository;
    private final EducationRepository educationRepository;

    @Transactional(readOnly = true)
    public List<ProfileListDto> getProfiles() {
        return profileRepository.findAll().stream().map(ProfileListDto::fromEntity).toList();
    }

    @Transactional(readOnly = true)
    public ProfileDetailDto getProfileById(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new BadRequestException("User not found"));
        Profile profile = profileRepository.findById(userId).orElseThrow(() -> new BadRequestException("Profile not found"));

        ProfileDetailDto profileDetailDto = ProfileDetailDto.builder()
                .user(profile.getUser())
                .bio(profile.getBio())
                .company(profile.getCompany())
                .website(profile.getWebsite())
                .location(profile.getLocation())
                .image(profile.getImage())
//                .skills(profile.getSkills().stream().map(Skill::getName).collect(Collectors.toList()))
                .experience(profile.getExperiences().stream().map(GetExperienceDto::from).collect(Collectors.toList()))
                .education(profile.getEducations().stream().map(GetEducationDto::fromEntity).collect(Collectors.toList()))
                .githubUsername(profile.getGithubUsername())
                .build();

        return profileDetailDto;
    }

    @Transactional
    public void addExperience(Long userId, ExperienceDto experienceDto) {
        User user = userRepository.findById(userId).orElseThrow(() -> new BadRequestException("User not found"));
        Profile profile = profileRepository.findByUser(user).orElseThrow(() -> new BadRequestException("Profile not found"));
        profile.addExperience(experienceDto.toEntity());
    }

    @Transactional
    public void deleteExperience(Long experienceId) {
        experienceRepository.deleteById(experienceId);
    }

    @Transactional
    public void addEducation(Long userId, EducationDto educationDto) {
        User user = userRepository.findById(userId).orElseThrow(() -> new BadRequestException("User not found"));
        Profile profile = profileRepository.findByUser(user).orElseThrow(() -> new BadRequestException("Profile not found"));
        profile.addEducation(educationDto.toEntity());
    }
    @Transactional
    public void deleteEducation(Long educationId) {
        educationRepository.deleteById(educationId);
    }

    @Transactional
    public void upsertProfile(long userId, UpsertProfileDto profileDto) {
        User user = userRepository.findById(userId).orElseThrow(() -> new BadRequestException("User not found"));
        Optional<Profile> optionalProfile = profileRepository.findByUser(user);
        if (optionalProfile.isPresent()) {
            Profile profile = optionalProfile.get();
            //ToDO 작성 더해야됨
        }
    }
}
