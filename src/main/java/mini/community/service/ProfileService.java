package mini.community.service;

import lombok.RequiredArgsConstructor;
import mini.community.domain.Profile;
import mini.community.domain.User;
import mini.community.dto.ExperienceDto;
import mini.community.dto.ProfileDetailDto;
import mini.community.dto.ProfileListDto;
import mini.community.dto.UpsertProfileDto;
import mini.community.repository.ExperienceRepository;
import mini.community.repository.ProfileRepository;
import mini.community.repository.UserRepository;
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

    @Transactional(readOnly = true)
    public List<ProfileListDto> getProfiles() {
        return profileRepository.findAll().stream().map(ProfileListDto::fromEntity).toList();
    }

    @Transactional(readOnly = true)
    public ProfileDetailDto getProfileById(Long id) {
        Profile profile = profileRepository.findById(id).orElseThrow(() -> new RuntimeException("Profile not found"));
        return ProfileDetailDto.fromEntity(profile);
    }

    @Transactional
    public void addExperience(Long userId, ExperienceDto experienceDto) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        Profile profile = profileRepository.findByUser(user).orElseThrow(() -> new RuntimeException("Profile not found"));
        profile.addExperience(experienceDto.toEntity());
    }

    @Transactional
    public void deleteExperience(Long experienceId) {
        experienceRepository.deleteById(experienceId);
    }

    @Transactional
    public void upsertProfile(long userId, UpsertProfileDto profileDto) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        Optional<Profile> optionalProfile = profileRepository.findByUser(user);
        if (optionalProfile.isPresent()) {
            Profile profile = optionalProfile.get();
            //ToDO 작성 더해야됨
        }
    }
}
