package mini.community.global.token;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.RequiredArgsConstructor;
import mini.community.dto.TokenDto;
import mini.community.dto.TokenResponseDto;
import mini.community.global.context.TokenContext;
import mini.community.global.context.TokenContextHolder;
import mini.community.global.exception.UsernameFromTokenException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import java.util.Date;


@Component
@RequiredArgsConstructor
public class TokenManager {

    @Value("${jwt.secret}")
    private String jwtSecret;
    @Value("${jwt.issuer}")
    private String jwtIssuer;

    public final static Long LOCAL_ACCESS_TOKEN_TIME_OUT = 99999L * 60 * 60;
    public final static Long REFRESH_TOKEN_TIME_OUT = 1000L * 60 * 60;

    public TokenResponseDto generateToken(TokenDto tokenDto) {
        String accessToken = newToken(tokenDto, LOCAL_ACCESS_TOKEN_TIME_OUT, "access");
        String refreshToken = newToken(tokenDto, REFRESH_TOKEN_TIME_OUT, "refresh");
        return new TokenResponseDto(accessToken, refreshToken);
    }

    public String newToken(TokenDto token, Long expireTime, String type) {
        long now = System.currentTimeMillis();
        return JWT.create()
                .withClaim("user_id", token.getUserId())
                .withClaim("type", type)
                .withIssuedAt(new Date(now))
                .withIssuer(jwtIssuer)
                .withExpiresAt(new Date(now + expireTime))
                .sign(Algorithm.HMAC512(jwtSecret));
    }

    public void validateToken(String token) {
        validateToken(token,"access");
    }

    public void validateToken(String token,String expectedType) {
        if (ObjectUtils.isEmpty(token)) {
            throw new UsernameFromTokenException("JWT Empty. Please check header");
        }

        JWTVerifier verifier = JWT.require(Algorithm.HMAC512(jwtSecret)).withIssuer(jwtIssuer).build();
        DecodedJWT jwt = verifier.verify(token);
        // type체크
        String type = jwt.getClaim("type").asString();
        if (expectedType!=null && !expectedType.equals(type)) {
            throw new UsernameFromTokenException("Invalid token type: " + type);
        }
        Claim claim = jwt.getClaim("user_id");
        Long userId = claim.asLong();
        if (userId == null) {
            throw new UsernameFromTokenException("Invalid token: " + token);
        }

        TokenContext context = TokenContextHolder.getContext();
        context.setUserId(userId);
        TokenContextHolder.setContext(context);
    }

    //
    public Long extractUserId(String token) {
        JWTVerifier verifier = JWT.require(Algorithm.HMAC512(jwtSecret)).withIssuer(jwtIssuer).build();
        DecodedJWT jwt = verifier.verify(token);
        return jwt.getClaim("user_id").asLong();
    }

    public long getTokenRemainingSeconds(String token) {
        try{
            DecodedJWT jwt = JWT.require(Algorithm.HMAC512(jwtSecret))
                    .withIssuer(jwtIssuer)
                    .build()
                    .verify(token);

            long expMillis = jwt.getExpiresAt().getTime(); // 만료시각(ms)
            long nowMillis = System.currentTimeMillis();

            long remaining = (expMillis - nowMillis) / 1000; // 초 단위 변환
            return Math.max(0, remaining); // 음수 방지
        } catch (Exception e) {
            return 0;
        }
    }

}
