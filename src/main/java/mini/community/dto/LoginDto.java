package mini.community.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
@Schema(description = "로그인 요청 DTO")
public class LoginDto {
    @Schema(description = "이메일 주소", example = "hong@example.com", required = true)
    private String email;
    @Schema(description = "비밀번호", example = "password123!", required = true)
    private String password;

    public LoginDto(String email, String password) {
        this.email = email;
        this.password = password;
    }

}
