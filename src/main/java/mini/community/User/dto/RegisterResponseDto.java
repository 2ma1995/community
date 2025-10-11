package mini.community.User.dto;

import lombok.Getter;

@Getter
public class RegisterResponseDto {
    private String token;

    public RegisterResponseDto(String token) {
        this.token = token;
    }
}
