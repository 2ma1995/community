package mini.community.Profile.service;

import lombok.RequiredArgsConstructor;
import mini.community.Profile.dto.SocialLinkDto;
import mini.community.Profile.dto.UpsertProfileDto;
import mini.community.Profile.entity.Profile;
import mini.community.Profile.entity.ProfileSkill;
import mini.community.Profile.entity.SocialLink;
import mini.community.User.domain.User;
import mini.community.education.domain.Education;
import mini.community.experience.domain.Experience;
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
import java.util.Set;
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
        userRepository.findById(userId).orElseThrow(() -> new BadRequestException("User not found"));
        Profile profile = profileRepository.findByUser_Id(userId).orElseThrow(() -> new BadRequestException("Profile not found"));

        return ProfileDetailDto.fromEntity(profile);
    }

    @Transactional
    public void addExperience(Long userId, ExperienceDto experienceDto) {
        User user = userRepository.findById(userId).orElseThrow(() -> new BadRequestException("User not found"));
        Profile profile = profileRepository.findByUser(user).orElseThrow(() -> new BadRequestException("Profile not found"));
        profile.addExperience(experienceDto.toEntity());
    }

    @Transactional
    public void deleteExperience(Long userId,Long experienceId) {
        Experience experience = experienceRepository.findById(experienceId).orElseThrow(() -> new BadRequestException("해당 경력이 존재하지 않습니다."));
        // 검증
        if (!experience.getProfile().getUser().getId().equals(userId)) {
            throw new BadRequestException("본인 소유의 경력만 삭제할 수 있습니다.");
        }
        experienceRepository.delete(experience);
    }

    @Transactional
    public void addEducation(Long userId, EducationDto educationDto) {
        User user = userRepository.findById(userId).orElseThrow(() -> new BadRequestException("User not found"));
        Profile profile = profileRepository.findByUser(user).orElseThrow(() -> new BadRequestException("Profile not found"));
        profile.addEducation(educationDto.toEntity());
    }

    @Transactional
    public void deleteEducation(Long userId,Long educationId) {
        Education education = educationRepository.findById(educationId).orElseThrow(() -> new BadRequestException("해당 교육이 존재하지 않습니다."));
        if (!education.getProfile().getUser().getId().equals(userId)) {
            throw new BadRequestException("본인 교육 경력만 삭제할수 있습니다.");
        }
        educationRepository.delete(education);
    }

    private void addSocialLinks(Profile profile, List<SocialLinkDto> socialLinkDtos) {
        if (socialLinkDtos == null || socialLinkDtos.isEmpty()) {
            return;
        }
        for (SocialLinkDto dto : socialLinkDtos) {
            SocialLink link = new SocialLink();
            link.setProfile(profile);
            link.setTwitter(dto.getTwitter());
            link.setFacebook(dto.getFacebook());
            link.setYoutube(dto.getYoutube());
            link.setLinkedin(dto.getLinkedin());

            profile.getSocialLinks().add(link);
        }
    }

    private List<Skill> resolveSkillsByNames(List<String> names){
        if (names == null || names.isEmpty()) return List.of();
        // 존재하는 스킬 조회
        List<Skill> existing = skillRepository.findByNameIn(names);
        // 존재하는 이름 집합
        Set<String> existingNames = existing.stream().map(Skill::getName).collect(Collectors.toSet());
        // 없는 이름만 추려서 생성
        List<Skill> toCreate = names.stream()
                .filter(name -> !existingNames.contains(name))
                .distinct()
                .map(n->Skill.builder().name(n).build())
                .toList();
        if (!toCreate.isEmpty()) existing.addAll(skillRepository.saveAll(toCreate));
        return existing;
        }


    @Transactional
    public void upsertProfile(long userId, UpsertProfileDto profileDto) {
        User user = userRepository.findById(userId).orElseThrow(() -> new BadRequestException("유저가 존재하지 않습니다."));

        Optional<Profile> optionalProfile = profileRepository.findByUser(user);
        List<Skill> skills = resolveSkillsByNames(profileDto.getSkills());


        if (optionalProfile.isPresent()) {
            Profile profile = optionalProfile.get();
            // 프로필 업데이트
            profile.update(profileDto);
            // 스킬 추가/수정
            profile.changeSkills(skills);
            // 소셜링크 제거후 추가
            profile.getSocialLinks().clear();
            addSocialLinks(profile, profileDto.getSocialLinks());
        } else {
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
            profile.changeSkills(skills);
            addSocialLinks(profile, profileDto.getSocialLinks());
            profileRepository.save(profile);
        }
    }
    //스킬 이름 목록 없는건 생성,있는건 재사용
}
