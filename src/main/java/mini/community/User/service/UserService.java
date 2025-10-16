package mini.community.User.service;

import lombok.RequiredArgsConstructor;
import mini.community.User.dto.RegisterDto;
import mini.community.User.dto.UserDto;
import mini.community.User.domain.User;
import mini.community.dto.*;
import mini.community.global.exception.BadRequestException;
import mini.community.global.token.TokenManager;
import mini.community.User.repository.UserRepository;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class UserService {
    private final TokenManager tokenManager;
    private final BCryptPasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final RedisTemplate<String ,String> redisTemplate;

    //redis key prefix
    private static final String REFRESH_KEY = "refresh:";
    private static final String SESSION_KEY = "session:";

    // 토큰 생성
    private TokenResponseDto toTokenResponseDto(User user) {
        TokenDto tokenDto = TokenDto.builder().userId(user.getId()).build();
        TokenResponseDto response = tokenManager.generateToken(tokenDto);

        // Refresh Token 저장(redis 7일)
        redisTemplate.opsForValue().set(
                REFRESH_KEY + user.getId(),
                response.getRefreshToken(),
                Duration.ofDays(7));

        // 세션 캐시 등록 (로그인 상태 1일 유지)
        redisTemplate.opsForValue().set(
                SESSION_KEY + user.getId(),
                "active",
                Duration.ofDays(1)
        );
        return response;
    }

    //회원 가입
    @Transactional
    public TokenResponseDto register(RegisterDto registerDto) {
        // email 중복 확인
        if (userRepository.existsByEmailAndDeletedFalse(registerDto.getEmail())) {
            throw new BadRequestException("이미 존재하는 이메일 입니다.");
        }
        // 비밀번호 일치 확인
        if (!registerDto.getPassword().equals(registerDto.getCheckPassword())) {
            throw new BadRequestException("비밀번호가 일치하지 않습니다.");
        }
        // 비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(registerDto.getPassword());
        // DTO -> Entity
        User user = registerDto.toEntity(encodedPassword);
        // 저장
        userRepository.save(user);
        //토큰 응답
        return toTokenResponseDto(user);
    }

    // 유저 삭제
    @Transactional
    public void deleteUser(Long userId) {
        User user = userRepository.findByIdAndDeletedFalse(userId).orElseThrow(() -> new BadRequestException("존재하지 않거나 이미 삭제된 유저입니다."));
        user.softDelete();
        userRepository.save(user);

        // 세션/토큰도 삭제
        logout(userId);
    }

    // 유저 조회
    @Transactional(readOnly = true)
    public UserDto getAuth(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new BadRequestException("유저를 찾을수 없습니다."));
        // entity -> dto
        return UserDto.fromEntity(user);
    }

    // 로그인 (중복 로그인 방지)
    @Transactional(readOnly = true)
    public TokenResponseDto login(LoginDto loginDto) {
        User user = userRepository.findByEmailAndDeletedFalse(loginDto.getEmail())
                .orElseThrow(() -> new BadRequestException("유저를 찾을수 없습니다."));
        if (!passwordEncoder.matches(loginDto.getPassword(), user.getPassword())) {
            throw new BadRequestException("비밀번호가 틀렸습니다.");
        }
        // 이미 로그인 중인 세션 확인
        String sessionKey = SESSION_KEY + user.getId();
        if (Boolean.TRUE.equals(redisTemplate.hasKey(sessionKey))) {
            throw new BadRequestException("이미 로그인 중인 사용자입니다.");
        }
        // 로그인 허용 후 세션+토큰 저장
        return toTokenResponseDto(user);
    }

    // refresh token 재발급
    @Transactional(readOnly = true)
    public TokenResponseDto reissueToken(Long userId, String refreshToken) {
        String saved = redisTemplate.opsForValue().get(REFRESH_KEY + userId);
        if (saved == null || !saved.equals(refreshToken)) {
            throw new BadRequestException("유효하지 않은 리프레시 토큰입니다.");
        }

        TokenDto tokenDto = TokenDto.builder().userId(userId).build();
        TokenResponseDto newTokens = tokenManager.generateToken(tokenDto);

        // 기존 refresh 갱신
        redisTemplate.opsForValue().set(
                REFRESH_KEY + userId,
                newTokens.getRefreshToken(),
                Duration.ofDays(7)
        );
        return newTokens;
    }

    //로그아웃 (redis refresh + 세션 삭제)
    @Transactional
    public void logout(Long userId) {
        redisTemplate.delete(REFRESH_KEY + userId);
        redisTemplate.delete(SESSION_KEY + userId);
    }

}
