package mini.community.controller;

import lombok.RequiredArgsConstructor;
import mini.community.User.dto.UserDto;
import mini.community.User.service.UserService;
import mini.community.dto.LoginDto;
import mini.community.dto.TokenResponseDto;
import mini.community.global.context.TokenContext;
import mini.community.global.context.TokenContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {
    private final UserService userService;

    @GetMapping
    public UserDto getAuth(){
        TokenContext context = TokenContextHolder.getContext();
        long userId = context.getUserId();
        return userService.getAuth(userId);
    }

    @PostMapping
    public TokenResponseDto login(@RequestBody LoginDto loginDto){
        return userService.login(loginDto);
    }
}
