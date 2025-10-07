package mini.community.global.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mini.community.global.token.TokenManager;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
//todo 전체적으로 수정 shouldNotFilter 확인해야함
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtTokenFilter extends OncePerRequestFilter {
    private final TokenManager tokenManager;
    private final static String AUTHORIZATION_HEADER = "x-auth-token";
    private static final List<String> EXCLUDED_URLS = Arrays.asList(
            "/login","/register","/public/**"
    );

    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain filterChain) throws ServletException, IOException, NullPointerException {
        String accessToken = this.resolveToken(req);
        try{
            tokenManager.validateToken(accessToken);
            filterChain.doFilter(req, res);
        } catch (Exception e) {
            log.error("토큰 검증 실패:{}", e.getMessage());
            res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            res.getWriter().write("Invalid or expired access token");
        }
    }
    private String resolveToken(HttpServletRequest request) {
        return request.getHeader(AUTHORIZATION_HEADER);
    }
}
