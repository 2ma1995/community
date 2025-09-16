package mini.community.controller;

import lombok.RequiredArgsConstructor;
import mini.community.dto.ProfileDetailDto;
import mini.community.dto.ProfileListDto;
import mini.community.global.context.TokenContext;
import mini.community.global.context.TokenContextHolder;
import mini.community.service.ProfileService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/profiles")
public class ProfileController {
    private final ProfileService profileService;

    @GetMapping
    public ResponseEntity<List<ProfileListDto>> getAllProfiles() {
        return ResponseEntity.ok(profileService.getProfiles());
    }

    @GetMapping("user/{userId}")
    public ResponseEntity<ProfileDetailDto> getProfileById(@PathVariable(value = "userId") Long userId) {
        return ResponseEntity.ok(profileService.getProfileById(userId));
    }

    @GetMapping("/me")
    public ProfileDetailDto getMyProfile() {
        TokenContext context = TokenContextHolder.getContext();
        long userId = context.getUserId();
        return profileService.getProfileById(userId);
    }

    @DeleteMapping("/experience/{experience_id}")
    public void deleteExperience(@PathVariable(value = "experience_id") Long experienceId) {
        profileService.deleteExperience(experienceId);
    }
}
