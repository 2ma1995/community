package mini.community.global.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import mini.community.global.token.TokenManager;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtTokenFilter extends OncePerRequestFilter {
    private final TokenManager tokenManager;
    private final static String AUTHORIZATION_HEADER = "x-auth-token";

    @Value("${except-uri}") //?
    private String exceptURI;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException, NullPointerException {
        // 토큰 조회
        String accessToken = this.resolveToken(request);

        // 토큰 벨리데이션
        tokenManager.validateToken(accessToken);
        filterChain.doFilter(request, response);
    }

    private String resolveToken(HttpServletRequest request) {
        return request.getHeader(AUTHORIZATION_HEADER);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getServletPath();
        String method = request.getMethod();

        AntPathMatcher matcher = new AntPathMatcher();

        if ("OPTIONS".equalsIgnoreCase(method)) return true;
        if ("/api/profiles".equals(path) && "GET".equalsIgnoreCase(method)) return true;
        if ("/api/profiles/image".equals(path) && "POST".equalsIgnoreCase(method)) return true;
        if (matcher.match("/api/profiles/user/**", path) && "GET".equalsIgnoreCase(method)) return true;
        if ("/api/auth".equals(path) && "POST".equalsIgnoreCase(method)) return true;
        if ("/api/users".equals(path) && "POST".equalsIgnoreCase(method)) return true;

        return Arrays.stream(exceptURI.split(",")).anyMatch(pattern -> matcher.match(pattern, path));
    }
}
