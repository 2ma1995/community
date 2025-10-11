package mini.community.User.controller;

import lombok.RequiredArgsConstructor;
import mini.community.User.dto.RegisterDto;
import mini.community.dto.TokenResponseDto;
import mini.community.User.service.UserService;
import mini.community.global.context.TokenContext;
import mini.community.global.context.TokenContextHolder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;

    @PostMapping
    public TokenResponseDto register(@RequestBody RegisterDto registerDto) {
        return userService.register(registerDto);
    }

    @DeleteMapping("/delete")
    public ResponseEntity<?> deleteMyAccount() {
        TokenContext context = TokenContextHolder.getContext();
        long userId = context.getUserId();

        userService.deleteUser(userId);
        return ResponseEntity.ok("유저 계정이 삭제되었습니다.");
    }

}
