package mini.community.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import mini.community.domain.Profile;
import mini.community.domain.ProfileSkill;
import mini.community.domain.Skill;
import mini.community.domain.User;
import mini.community.dto.ProfileDto;
import mini.community.dto.UserDto;
import mini.community.repository.ProfileRepository;
import org.apache.coyote.BadRequestException;
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
        return profileRepository.findAll().stream().map(ProfileDto::fromEntity).collect(Collectors.toList());
    }
}
