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
import mini.community.global.exception.UserNameFromTokenException;
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
    public final static Long ACCESS_TOKEN_TIME_OUT = 1000L * 60 * 60;

    public TokenResponseDto generateToken(TokenDto tokenDto) {
        String token = newToken(tokenDto, LOCAL_ACCESS_TOKEN_TIME_OUT);
        return new TokenResponseDto(token);
    }

    public String newToken(TokenDto token, Long expireTime) {
        return JWT.create()
                .withClaim("user_id", token.getUserId())
                .withIssuedAt(new Date())
                .withIssuer(jwtIssuer)
                .withExpiresAt(new Date(System.currentTimeMillis() + expireTime))
                .sign(Algorithm.HMAC512(jwtSecret));
    }

    public void validateToken(String token) {
        if (ObjectUtils.isEmpty(token)) {
            throw new UserNameFromTokenException("JWT Empty. Please check header");
        }
        JWTVerifier verifier = JWT.require(Algorithm.HMAC512(jwtSecret)).withIssuer(jwtIssuer).build();
        DecodedJWT jwt = verifier.verify(token);
        TokenContext context = TokenContextHolder.getContext();
        Claim claim = jwt.getClaim("user_id");
        context.setUserId(claim.asLong());

        TokenContextHolder.setContext(context);
    }
}
