package mini.community.Profile.controller;

import lombok.RequiredArgsConstructor;
import mini.community.Profile.dto.UpsertProfileDto;
import mini.community.Profile.service.ImageService;
import mini.community.education.dto.EducationDto;
import mini.community.experience.dto.ExperienceDto;
import mini.community.Profile.dto.ProfileDetailDto;
import mini.community.global.context.TokenContext;
import mini.community.global.context.TokenContextHolder;
import mini.community.Profile.service.ProfileService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/profiles")
public class ProfileController {
    private final ProfileService profileService;
    private final ImageService imageService;

    @GetMapping
    public ResponseEntity<?> getAllProfiles() {
        return ResponseEntity.ok(profileService.getProfiles());
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getProfileById(@PathVariable(value = "userId") final Long userId) {
        return ResponseEntity.ok(profileService.getProfileById(userId));
    }

    @GetMapping("/me")
    public ResponseEntity<?> getMyProfile() {
        TokenContext context = TokenContextHolder.getContext();
        long userId = context.getUserId();
        ProfileDetailDto profile = profileService.getProfileById(userId);
        if (profile != null) {
            return ResponseEntity.ok(profile);
        }else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public ResponseEntity<?> upsertProfile(@RequestBody UpsertProfileDto profileDto) {
        TokenContext context = TokenContextHolder.getContext();
        long userId = context.getUserId();
            profileService.upsertProfile(userId, profileDto);
            return ResponseEntity.status(HttpStatus.CREATED).body("Profile 생성/업데이트 완료");
    }

    @PutMapping("/experience")
    public void addExperience(@RequestBody ExperienceDto experienceDto) {
        TokenContext context = TokenContextHolder.getContext();
        long userId = context.getUserId();
        profileService.addExperience(userId, experienceDto);
    }

    @DeleteMapping("/experience/{experience_id}")
    public void deleteExperience(@PathVariable(value = "experience_id") Long experienceId) {
        profileService.deleteExperience(experienceId);
    }

    @PutMapping("/education")
    public void addEducation(@RequestBody EducationDto educationDto) {
        TokenContext context = TokenContextHolder.getContext();
        long userId = context.getUserId();
        profileService.addEducation(userId, educationDto);
    }

    @DeleteMapping("/education/{education_id}")
    public void deleteEducation(@PathVariable(value = "education_id") Long educationId) {
        profileService.deleteEducation(educationId);
    }

    @PostMapping("/image")
    public void saveProfileImage(@ModelAttribute(name = "file") MultipartFile file) {
        imageService.saveImage(file);
    }

}
