package mini.community.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
public class RefreshRequestDto {
    @Schema(description = "리프레시 토큰", required = true)
    private String refreshToken;
}
