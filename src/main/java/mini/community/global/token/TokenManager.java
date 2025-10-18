package mini.community.global.token;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mini.community.dto.TokenDto;
import mini.community.dto.TokenResponseDto;
import mini.community.global.context.TokenContext;
import mini.community.global.context.TokenContextHolder;
import mini.community.global.exception.UsernameFromTokenException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;


@Slf4j
@Component
@RequiredArgsConstructor
public class TokenManager {

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.issuer}")
    private String jwtIssuer;

    public static final Long LOCAL_ACCESS_TOKEN_TIME_OUT = 1000L * 60 * 60 * 24; //24시간
    public static final Long REFRESH_TOKEN_TIME_OUT = 1000L * 60 * 60 * 24 * 7; // 7일

    // 토큰 생성
    public TokenResponseDto generateToken(TokenDto tokenDto) {
        String accessToken = newToken(tokenDto, LOCAL_ACCESS_TOKEN_TIME_OUT, "access");
        String refreshToken = newToken(tokenDto, REFRESH_TOKEN_TIME_OUT, "refresh");
        return new TokenResponseDto(accessToken, refreshToken);
    }

    // 개별 토큰 생성
    public String newToken(TokenDto token, Long expireSecond, String type) {
        long now = System.currentTimeMillis();
        return JWT.create()
                .withClaim("user_id", token.getUserId())
                .withClaim("type", type)
                .withIssuedAt(new Date(now))
                .withExpiresAt(new Date(now + expireSecond))
                .withIssuer(jwtIssuer)
                .sign(Algorithm.HMAC512(jwtSecret));
    }

    // 토큰 검증 ( access 전용)
    public void validateToken(String token) {
        validateToken(token, "access");
    }

    // 토큰 검증( access / refresh 공용)
    public void validateToken(String token, String expectedType) {
        if (token == null || token.isBlank()) {
            throw new UsernameFromTokenException("JWT Empty. Please check header (x-auth-token)");
        }
        try {
            JWTVerifier verifier = JWT
                    .require(Algorithm.HMAC512(jwtSecret))
                    .withIssuer(jwtIssuer)
                    .build();
            DecodedJWT jwt = verifier.verify(token);

            // 토큰 type 체크
            String type = jwt.getClaim("type").asString();
            if (expectedType != null && !expectedType.equals(type)) {
                throw new UsernameFromTokenException("Invalid token type: " + type);
            }

            // user_id 추출
            Long userId = jwt.getClaim("user_id").asLong();
            if (userId == null || userId <= 0) {
                throw new UsernameFromTokenException("Invalid user_id in token: " + token);
            }
            // Context 세팅(초기화 과정, 매 요청마다 새TokenContext로 세팅/ 스레드로컬 오염 방지)
            TokenContext context = new TokenContext();
            context.setUserId(userId);
            TokenContextHolder.setContext(context);
            log.debug("Token validated. user_id={} type={}", userId, type);

        } catch (com.auth0.jwt.exceptions.TokenExpiredException e) {
            // 만료된 토큰의 경우에도 userId를 추출해서 로그아웃 가능하게 허용
            DecodedJWT jwt = JWT.decode(token);
            Long userId = jwt.getClaim("user_id").asLong();
            TokenContext ctx = new TokenContext();
            ctx.setUserId(userId);
            TokenContextHolder.setContext(ctx);
        }
    }


    // 토큰에서 user_id만 추출
    public Long extractUserId(String token) {
        DecodedJWT jwt = JWT
                .require(Algorithm.HMAC512(jwtSecret))
                .withIssuer(jwtIssuer)
                .build()
                .verify(token);
        return jwt.getClaim("user_id").asLong();
    }

    // 남은 만료 시간 계산
    public long getTokenRemainingSeconds(String token) {
        try {
            DecodedJWT jwt = JWT
                    .require(Algorithm.HMAC512(jwtSecret))
                    .withIssuer(jwtIssuer)
                    .build()
                    .verify(token);

            long expMillis = jwt.getExpiresAt().getTime(); // 만료시각(ms)
            long nowMillis = System.currentTimeMillis();
            long remaining = (expMillis - nowMillis) / 1000;
            return Math.max(0, remaining); //초 단위 변환/ 음수 방지
        } catch (Exception e) {
            return 0;
        }
    }
}
