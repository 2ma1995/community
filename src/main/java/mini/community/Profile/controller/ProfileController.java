package mini.community.Profile.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.SchemaProperty;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import mini.community.Profile.dto.ProfileListDto;
import mini.community.Profile.dto.UpsertProfileDto;
import mini.community.Profile.service.ImageService;
import mini.community.education.dto.EducationDto;
import mini.community.experience.dto.ExperienceDto;
import mini.community.Profile.dto.ProfileDetailDto;
import mini.community.global.context.TokenContext;
import mini.community.global.context.TokenContextHolder;
import mini.community.Profile.service.ProfileService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회성공",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ProfileListDto.class))),
    })
    @GetMapping
    public ResponseEntity<?> getAllProfiles() {
        return ResponseEntity.ok(profileService.getProfiles());
    }

    @Operation(summary = "id로 프로필 조회", description = "userId로 유저 프로필 조회")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회성공",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ProfileDetailDto.class))),
            @ApiResponse(responseCode = "404", description = "프로필 / 유저 없음")
    })
    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getProfileById(@Parameter(description = "유저 ID", example = "1") @PathVariable(value = "userId") final Long userId) {
        return ResponseEntity.ok(profileService.getProfileById(userId));
    }

    @Operation(
            summary = "내 프로필 조회",
            description = "jwt토큰 통해 현재 로그인된 사용자의 프로필 조회",
            security = @SecurityRequirement(name = "jwt")
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ProfileDetailDto.class))),
            @ApiResponse(responseCode = "401", description = "인증 실패(토큰 없음/무효)"),
            @ApiResponse(responseCode = "404", description = "프로필 없음")
    })
    @GetMapping("/me")
    public ResponseEntity<?> getMyProfile() {
        TokenContext context = TokenContextHolder.getContext();
        long userId = context.getUserId();
        ProfileDetailDto profile = profileService.getProfileById(userId);
        if (profile != null) {
            return ResponseEntity.ok(profile);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "프로필 생성/업데이트", description = "사용자의 기본 프로필 정보를 생성하거나 업데이트합니다.", security = @SecurityRequirement(name = "jwt"))
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            required = true,
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = UpsertProfileDto.class),
                    examples = @ExampleObject(name = "예시",
                            value = """
                                    {
                                      "status": "Junior Developer",
                                      "company": "SK Hynix",
                                      "website": "https://www.hynix.com",
                                      "location": "Bucheon",
                                      "bio": "백엔드를 공부하고 있어요",
                                      "image": "https://example.com/image.jpg",
                                      "githubUsername": "hong123",
                                      "skills": ["Java","Spring","JPA"],
                                      "socialLinks": [
                                        {"twitter":"@hong","facebook":"hong.fb","youtube":"hong-yt","linkedin":"hong-li"}
                                      ]
                                    }
                                    """))
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "생성/업데이트 완료"),
            @ApiResponse(responseCode = "400", description = "유효성 오류"),
            @ApiResponse(responseCode = "401", description = "인증 실패")
    })
    @PostMapping
    public ResponseEntity<?> upsertProfile(@RequestBody UpsertProfileDto profileDto) {
        TokenContext context = TokenContextHolder.getContext();
        long userId = context.getUserId();
        profileService.upsertProfile(userId, profileDto);
        return ResponseEntity.status(HttpStatus.CREATED).body("Profile 생성/업데이트 완료");
    }

    @Operation(
            summary = "경력 추가 API",
            security = @io.swagger.v3.oas.annotations.security.SecurityRequirement(name = "jwt"),
            description = "나의 경력 추가."
    )
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            required = true,
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ExperienceDto.class),
                    examples = @ExampleObject(value = """
                            {
                              "title": "Backend Developer",
                              "company": "ABC",
                              "fromDate": "2023-01-01",
                              "toDate": "2024-03-31",
                              "current": false,
                              "description": "Spring/JPA 기반 API 개발"
                            }
                            """))
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "추가 성공"),
            @ApiResponse(responseCode = "401", description = "인증 실패")
    })
    @PutMapping("/experience")
    public void addExperience(@RequestBody ExperienceDto experienceDto) {
        TokenContext context = TokenContextHolder.getContext();
        long userId = context.getUserId();
        profileService.addExperience(userId, experienceDto);
    }

    @Operation(
            summary = "경력 삭제",
            security = @io.swagger.v3.oas.annotations.security.SecurityRequirement(name = "jwt")
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "삭제 성공"),
            @ApiResponse(responseCode = "401", description = "인증 실패")
    })
    @DeleteMapping("/experience/{experience_id}")
    public ResponseEntity<?> deleteExperience(@PathVariable(value = "experience_id") Long experienceId) {
        long userId = TokenContextHolder.getContext().getUserId();
        profileService.deleteExperience(userId, experienceId);
        return ResponseEntity.ok("경력이 삭제되었습니다.");

    }

    @Operation(
            summary = "학력 추가",
            security = @io.swagger.v3.oas.annotations.security.SecurityRequirement(name = "jwt")
    )
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            required = true,
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = EducationDto.class),
                    examples = @ExampleObject(value = """
                            {
                              "school": "SNU",
                              "degree": "B.S.",
                              "fieldOfStudy": "Computer Science",
                              "fromDate": "2018-03-01",
                              "toDate": "2022-02-28",
                              "current": false,
                              "description": "자료구조/알고리즘 전공"
                            }
                            """))
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "추가 성공"),
            @ApiResponse(responseCode = "401", description = "인증 실패")
    })
    @PutMapping("/education")
    public void addEducation(@RequestBody EducationDto educationDto) {
        TokenContext context = TokenContextHolder.getContext();
        long userId = context.getUserId();
        profileService.addEducation(userId, educationDto);
    }

    @Operation(
            summary = "학력 삭제",
            security = @io.swagger.v3.oas.annotations.security.SecurityRequirement(name = "jwt")
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "삭제 성공"),
            @ApiResponse(responseCode = "401", description = "인증 실패")
    })
    @DeleteMapping("/education/{education_id}")
    public ResponseEntity<?> deleteEducation(@PathVariable(value = "education_id") Long educationId) {
        long userId = TokenContextHolder.getContext().getUserId();
        profileService.deleteEducation(userId, educationId);
        return ResponseEntity.ok("교육이 삭제되었습니다.");
    }

    @Operation(
            summary = "프로필 이미지 업로드",
            description = "프로필 이미지를 업로드합니다. (multipart/form-data)",
            security = @io.swagger.v3.oas.annotations.security.SecurityRequirement(name = "jwt")
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "업로드 성공"),
            @ApiResponse(responseCode = "400", description = "파일 오류"),
            @ApiResponse(responseCode = "401", description = "인증 실패")
    })
    @PostMapping(value = "/image",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public void saveProfileImage(
            @Parameter(description = "업로드할 이미지 파일", required = true, content = @Content(mediaType = MediaType.MULTIPART_FORM_DATA_VALUE, schema = @Schema(type = "string", format = "binary")))
            @RequestPart("file") MultipartFile file) {
        imageService.saveImage(file);
    }

}
