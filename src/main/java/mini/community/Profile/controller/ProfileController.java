package mini.community.Profile.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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

@Tag(name = "프로필 API", description = "유저 프로필, 학력, 경력 정보를 관리하는 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/profiles")
public class ProfileController {
    private final ProfileService profileService;
    private final ImageService imageService;

    @Operation(summary = "모든 프로필 조회", description = "등록된 모든 유저의 프로필 목록 조회")
    @GetMapping
    public ResponseEntity<?> getAllProfiles() {
        return ResponseEntity.ok(profileService.getProfiles());
    }

    @Operation(summary = "id로 프로필 조회", description = "userId로 유저 프로필 조회")
    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getProfileById(@PathVariable(value = "userId") final Long userId) {
        return ResponseEntity.ok(profileService.getProfileById(userId));
    }

    @Operation(summary = "내 프로필 조회", description = "jwt토큰 통해 현재 로그인된 사용자의 프로필 조회")
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

    @Operation(summary = "프로필 생성/업데이트", description = "사용자의 기본 프로필 정보를 생성하거나 업데이트합니다.")
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
