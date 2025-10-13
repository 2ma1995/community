package mini.community.User.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import mini.community.User.dto.RegisterDto;
import mini.community.dto.TokenResponseDto;
import mini.community.User.service.UserService;
import mini.community.global.context.TokenContext;
import mini.community.global.context.TokenContextHolder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
@Tag(name = "User API", description = "회원가입 관련 API")
public class UserController {
    private final UserService userService;

    @Operation(
            summary = "회원가입",
            description = "새로운 사용자를 등록합니다.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "회원가입 정보",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = RegisterDto.class),
                            examples = @ExampleObject(
                                    name = "회원가입 예제",
                                    value = """
                        {
                          "name": "홍길동",
                          "email": "hong@example.com",
                          "password": "password123!",
                          "checkPassword": "password123!"
                        }
                        """
                            )
                    )
            )
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "회원가입 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = TokenResponseDto.class),
                            examples = @ExampleObject(
                                    name = "성공 응답 예제",
                                    value = """
                        {
                          "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
                          "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
                        }
                        """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "잘못된 요청 (비밀번호 불일치, 이메일 중복 등)",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
                        {
                          "error": "비밀번호가 일치하지 않습니다."
                        }
                        """
                            )
                    )
            )
    })
    @PostMapping("/register")
    public TokenResponseDto register(@RequestBody RegisterDto registerDto) {
        return userService.register(registerDto);
    }

    @Operation(
            summary = "회원 탈퇴",
            description = "현재 로그인한 사용자의 계정을 삭제합니다. JWT 토큰이 필요합니다.",
            security = @io.swagger.v3.oas.annotations.security.SecurityRequirement(name = "jwt token")
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "회원 탈퇴 성공",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = "\"유저 계정이 삭제되었습니다.\""
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
    @DeleteMapping("/delete")
    public ResponseEntity<?> deleteMyAccount() {
        TokenContext context = TokenContextHolder.getContext();
        long userId = context.getUserId();

        userService.deleteUser(userId);
        return ResponseEntity.ok("유저 계정이 삭제되었습니다.");
    }

}
