package mini.community.User.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import mini.community.User.dto.UserDto;
import mini.community.User.service.UserService;
import mini.community.dto.LoginDto;
import mini.community.dto.RefreshRequestDto;
import mini.community.dto.TokenDto;
import mini.community.dto.TokenResponseDto;
import mini.community.global.context.TokenContext;
import mini.community.global.context.TokenContextHolder;
import mini.community.global.token.TokenBlacklistService;
import mini.community.global.token.TokenManager;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
@Tag(name = "Auth API", description = "인증 관련 API")
public class AuthController {
    private final UserService userService;
    private final TokenManager tokenManager;
    private final TokenBlacklistService tokenBlacklistService;

    @Operation(
            summary = "현재 로그인한 사용자 정보 조회",
            description = "JWT 토큰으로 현재 로그인한 사용자의 정보를 조회합니다.",
            security = @io.swagger.v3.oas.annotations.security.SecurityRequirement(name = "jwt token")
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "사용자 정보 조회 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = UserDto.class),
                            examples = @ExampleObject(
                                    name = "성공 응답 예제",
                                    value = """
                        {
                          "id": 1,
                          "name": "홍길동",
                          "email": "hong@example.com"
                        }
                        """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "인증 실패 (토큰 없음 또는 유효하지 않은 토큰)",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
                        {
                          "error": "인증이 필요합니다."
                        }
                        """
                            )
                    )
            )
    })
    @GetMapping
    public UserDto getAuth(){
        TokenContext context = TokenContextHolder.getContext();
        long userId = context.getUserId();
        return userService.getAuth(userId);
    }

    @Operation(
            summary = "로그인",
            description = "이메일과 비밀번호로 로그인하여 JWT 토큰을 받습니다.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "로그인 정보",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = LoginDto.class),
                            examples = @ExampleObject(
                                    name = "로그인 예제",
                                    value = """
                        {
                          "email": "hong@example.com",
                          "password": "password123!"
                        }
                        """
                            )
                    )
            )
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "로그인 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = TokenResponseDto.class),
                            examples = @ExampleObject(
                                    name = "성공 응답 예제",
                                    value = """
                        {
                          "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
                        }
                        """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "로그인 실패 (이메일 또는 비밀번호 오류)",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
                        {
                          "error": "유저를 찾을수 없습니다."
                        }
                        """
                            )
                    )
            )
    })
    @PostMapping("/login")
    public TokenResponseDto login(@RequestBody LoginDto loginDto){
        return userService.login(loginDto);
    }

    @PostMapping("/refresh")
    @Operation(summary = "토큰 재발급", description = "리프레시 토큰으로 액세스/리프레시 토큰 재발급")
    public TokenResponseDto refresh(@RequestBody RefreshRequestDto req) {
        // 1) refresh 토큰 유효성/타입 검증
        tokenManager.validateToken(req.getRefreshToken(), "refresh");
        Long userId = tokenManager.extractUserId(req.getRefreshToken());
        // 2) userId로 새 토큰 발급
        return userService.reissueToken(userId, req.getRefreshToken());
    }

    @PostMapping("/logout")
    public String logout(@RequestHeader("x-auth-token") String token) {
        // Access Token 남은 만료 시간 계산
        long expireSeconds = tokenManager.getTokenRemainingSeconds(token);

        // 블랙리스트에 등록 (남은 유효시간만큼 TTL 유지)
        tokenBlacklistService.blacklistToken(token, expireSeconds);

        // Refresh & Session 삭제 (Redis)
        TokenContext context = TokenContextHolder.getContext();
        userService.logout(context.getUserId());

        return "로그아웃 되었습니다.";
    }
}
