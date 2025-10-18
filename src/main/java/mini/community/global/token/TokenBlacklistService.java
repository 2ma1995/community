package mini.community.global.token;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class TokenBlacklistService {

    private final RedisTemplate<String, String > redisTemplate;
    private final TokenManager tokenManager;

    private static final String BLACKLIST_PREFIX = "blacklist:";
    // 토큰 만료 TTL을 직접 계산하여 블랙리스트 등록
    // (AuthController에서 직접 ttl 계산해서 전달하는 경우)
    public void blacklistToken(String token, long ttlSeconds) {
        if (ttlSeconds <= 0) ttlSeconds = 60; // 최소 1분 보관
        redisTemplate.opsForValue().set(BLACKLIST_PREFIX + token, "logout", Duration.ofSeconds(ttlSeconds));
    }

    // 내부에서 ttl을 자동 계산하는 기본형 (별도 ttl인자 x)
    public void blacklistToken(String token) {
        long ttl = tokenManager.getTokenRemainingSeconds(token);
        // ✅ 만료된 토큰이면 최소 1분 유지로 블랙리스트 등록
        if (ttl <= 0) {
            ttl = 60;
        }
        redisTemplate.opsForValue().set(BLACKLIST_PREFIX + token, "logout", Duration.ofSeconds(ttl));
    }

    public boolean isBlacklisted(String token) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(BLACKLIST_PREFIX + token));
    }
}
