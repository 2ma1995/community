package mini.community.User.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import mini.community.User.domain.User;

@Getter
@Setter
@Schema(description = "회원가입 요청 DTO")
public class RegisterDto {
    @Schema(description = "사용자 이름", example = "홍길동", required = true)
    private String name;
    @Schema(description = "이메일 주소", example = "hong@example.com", required = true)
    private String email;
    @Schema(description = "비밀번호", example = "password123!", required = true)
    private String password;
    @Schema(description = "비밀번호 확인", example = "password123!", required = true)
    private String checkPassword;

    public User toEntity(String encodedPassword) {
        return User.builder()
                .name(this.name)
                .email(this.email)
                .password(encodedPassword)
                .build();
    }

}
