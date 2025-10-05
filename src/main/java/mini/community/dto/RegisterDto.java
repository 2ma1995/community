package mini.community.dto;

import lombok.Getter;
import lombok.ToString;
import mini.community.User.entity.User;

@Getter
@ToString
public class RegisterDto {
    private String name;
    private String email;
    private String password;

    public RegisterDto(String name, String email, String password) {
        this.name = name;
        this.email = email;
        this.password = password;
    }
    public User toEntity() {
        return User.builder()
                .name(this.name)
                .email(this.email)
                .password(this.password)
                .build();
    }
}
