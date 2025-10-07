package mini.community.User.dto;

import lombok.Builder;
import lombok.Getter;
import mini.community.User.entity.User;

@Getter
public class UserDto {

    private Long id;
    private String name;
    private String email;
    private String password;

    @Builder
    public UserDto(Long id, String name, String email, String password) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
    }

    public static UserDto fromEntity(User user) {
        return UserDto.builder()
                .id(user.getId())
                .name(user.getUsername())
                .email(user.getEmail())
                .password(user.getPassword())
                .build();
    }
}
