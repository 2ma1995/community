package mini.community.global.exception.handler;

import lombok.extern.slf4j.Slf4j;
import mini.community.global.exception.BadRequestException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Arrays;
import java.util.Optional;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    //BadRequestException handler
    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<?> handleBadRequestException(BadRequestException e){
        String message = Optional.ofNullable(e.getMessage()).orElseGet(() -> "올바른 요청이 아닙니다. ");
        ErrorDetailResponse detailResponse = ErrorDetailResponse.builder().message(message).build();
        ErrorResponse response = ErrorResponse.builder()
                .errors(Arrays.asList(detailResponse))
                .build();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

}
