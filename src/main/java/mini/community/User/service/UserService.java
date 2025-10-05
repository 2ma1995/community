package mini.community.User.service;

import lombok.RequiredArgsConstructor;
import mini.community.User.dto.UserDto;
import mini.community.User.entity.User;
import mini.community.dto.*;
import mini.community.global.exception.BadRequestException;
import mini.community.global.token.TokenManager;
import mini.community.User.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final TokenManager tokenManager;
    private final BCryptPasswordEncoder passwordEncoder;
    private UserRepository userRepository;


    private TokenResponseDto toTokenResponseDto(User user) {
        TokenDto tokenDto = TokenDto.builder().userId(user.getId()).build();
        return tokenManager.generateToken(tokenDto);
    }

    @Transactional
    public TokenResponseDto register(RegisterDto registerDto) {
        if(userRepository.existsByEmail(registerDto.getEmail())) {
            throw new BadRequestException("user already exists");
        }
        User user = userRepository.save(User.builder()
                        .name(registerDto.getName())
                        .email(registerDto.getEmail())
                        .password(passwordEncoder.encode(registerDto.getPassword()))
                .build());
        return toTokenResponseDto(user);
    }

    @Transactional(readOnly = true)
    public UserDto getAuth(Long userId){
        User user = userRepository.findById(userId).orElseThrow(
                ()->new BadRequestException("user not found")
        );
        return UserDto.builder()
                .id(user.getId())
                .name(user.getUsername())
                .email(user.getEmail())
                .build();
    }

    @Transactional(readOnly = true)
    public TokenResponseDto login(LoginDto loginDto) {
        User user = userRepository.findByEmail(loginDto.getEmail()).orElseThrow(
                ()->new BadRequestException("유저를 찾을수 없습니다.")
        );
        if (!passwordEncoder.matches(loginDto.getPassword(), user.getPassword())) {
            throw new BadRequestException("비밀번호가 틀렸습니다.");
        }
        return toTokenResponseDto(user);
    }

}
