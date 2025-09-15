package mini.community.controller;

import lombok.RequiredArgsConstructor;
import mini.community.domain.Profile;
import mini.community.dto.ProfileDto;
import mini.community.repository.ProfileRepository;
import mini.community.service.ProfileService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/profiles")
public class ProfileController {
    private final ProfileService profileService;

    @GetMapping
    public ResponseEntity<List<ProfileDto>> getAllProfiles() {
        return ResponseEntity.ok(profileService.getProfiles());
    }

//    @GetMapping("user/{userId}")
//    public ResponseEntity<ProfileDto> getProfileById(@PathVariable(value = "userId") Long userId) {
//        return ProfileService.getProfileById(userId);
//    }


}
