package mini.community.global.exception.handler;

import lombok.Builder;
import lombok.Getter;

@Getter
public class ErrorDetailResponse {
    private String message;

    @Builder
    public ErrorDetailResponse(String message){
        this.message = message;
    }
}
