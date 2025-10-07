package mini.community.Profile.service;

import lombok.RequiredArgsConstructor;
import mini.community.Profile.dto.UpsertProfileDto;
import mini.community.Profile.entity.Profile;
import mini.community.Profile.entity.ProfileSkill;
import mini.community.User.entity.User;
import mini.community.skill.domain.Skill;
import mini.community.education.dto.GetEducationDto;
import mini.community.experience.dto.ExperienceDto;
import mini.community.Profile.dto.ProfileDetailDto;
import mini.community.Profile.dto.ProfileListDto;
import mini.community.education.dto.EducationDto;
import mini.community.experience.dto.GetExperienceDto;
import mini.community.global.exception.BadRequestException;
import mini.community.education.repository.EducationRepository;
import mini.community.experience.repository.ExperienceRepository;
import mini.community.Profile.repository.ProfileRepository;
import mini.community.User.repository.UserRepository;
import mini.community.skill.repository.SkillRepository;
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
    private final SkillRepository skillRepository;

    @Transactional(readOnly = true)
    public List<ProfileListDto> getProfiles() {
        return profileRepository.findAll().stream().map(ProfileListDto::fromEntity).toList();
    }

    @Transactional(readOnly = true)
    public ProfileDetailDto getProfileById(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new BadRequestException("User not found"));
        Profile profile = profileRepository.findByUserId(userId).orElseThrow(() -> new BadRequestException("Profile not found"));

        ProfileDetailDto profileDetailDto = ProfileDetailDto.builder()
                .user(profile.getUser())
                .bio(profile.getBio())
                .company(profile.getCompany())
                .website(profile.getWebsite())
                .location(profile.getLocation())
                .image(profile.getImage())
                .skills(profile.getProfileSkills().stream().map(ProfileSkill::getSkill).collect(Collectors.toList()))
                .experience(profile.getExperiences().stream().map(GetExperienceDto::fromEntity).collect(Collectors.toList()))
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
            // 프로필 업데이트
            profile.update(profileDto);

            // 스킬 추가/수정
            List<Skill> skills = skillRepository.findByNameIn(profileDto.getSkills());
            profile.changeSkills(skills);
        } else{
            //새 프로필 생성
            Profile profile = Profile.builder()
                    .user(user)
                    .status(profileDto.getStatus())
                    .company(profileDto.getCompany())
                    .website(profileDto.getWebsite())
                    .location(profileDto.getLocation())
                    .bio(profileDto.getBio())
                    .image(profileDto.getImage())
                    .githubUsername(profileDto.getGithubUsername())
                    .build();
            profileRepository.save(profile);

            //스킬추가
            List<Skill> skills = skillRepository.findByNameIn(profileDto.getSkills());
            profile.changeSkills(skills);
        }
    }

}
