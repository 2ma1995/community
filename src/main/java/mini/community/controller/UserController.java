package mini.community.controller;

import lombok.RequiredArgsConstructor;
import mini.community.dto.RegisterDto;
import mini.community.dto.TokenResponseDto;
import mini.community.dto.UserDto;
import mini.community.repository.UserRepository;
import mini.community.service.UserService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
