package mini.community.User.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import mini.community.User.domain.User;
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class UserDto {

    private Long id;
    private String name;
    private String email;
    private String password;

    public static UserDto fromEntity(User user) {
        return UserDto.builder()
                .id(user.getId())
                .name(user.getUsername())
                .email(user.getEmail())
                .password(user.getPassword())
                .build();
    }
}
