package mini.community.User.controller;

import lombok.RequiredArgsConstructor;
import mini.community.dto.RegisterDto;
import mini.community.dto.TokenResponseDto;
import mini.community.User.service.UserService;
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

}
