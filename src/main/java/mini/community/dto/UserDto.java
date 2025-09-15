package mini.community.dto;

import lombok.Builder;
import lombok.Getter;
import mini.community.domain.User;

@Getter
public class UserDto {

    private Long id;
    private String username;
    private String email;

    @Builder
    public UserDto(Long id, String username, String email) {
        this.id = id;
        this.username = username;
        this.email = email;
    }

    public static UserDto from(User user) {
        return UserDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .build();
    }
}
