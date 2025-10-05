package mini.community.User.dto;

import lombok.Builder;
import lombok.Getter;
import mini.community.User.entity.User;

@Getter
public class UserDto {

    private Long id;
    private String name;
    private String email;

    @Builder
    public UserDto(Long id, String name, String email) {
        this.id = id;
        this.name = name;
        this.email = email;
    }

    public static UserDto fromEntity(User user) {
        return UserDto.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .build();
    }
}
