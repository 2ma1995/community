package mini.community.User.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import mini.community.User.domain.User;
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Schema(description = "유저 정보 DTO")
public class UserDto {
    @Schema(description = "유저 id")
    private Long id;
    @Schema(description = "유저 닉네임")
    private String name;
    @Schema(description = "유저 이메일")
    private String email;
    @Schema(description = "유저 비밀번호")
    private String password;

    public static UserDto fromEntity(User user) {
        return UserDto.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .build();
    }
}
