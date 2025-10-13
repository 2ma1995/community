package mini.community.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
@Schema(description = "회원가입/로그인 응답 DTO")
public class TokenResponseDto {
    @Schema(description = "JWT 액세스 토큰",
    example = "eyJhbGciOiJIUzI1NiIs...")
    private String accessToken;
    @Schema(description = "JWT 리프레시 토큰",
            example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
    private String refreshToken;
    public TokenResponseDto(String accessToken,String refreshToken) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }
}
