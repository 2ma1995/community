package mini.community.global.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mini.community.global.token.TokenManager;
import org.springframework.beans.factory.annotation.Value;
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

    @Value("${except-uri}")
    private String exceptURI;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException, NullPointerException {
        // 토큰 조회
        String accessToken = this.resolveToken(request);

        if (accessToken != null && !accessToken.isEmpty()) {
            try{
                tokenManager.validateToken(accessToken);
            }catch (Exception e){
                log.error("토큰 검증 실퍠: {}", e.getMessage());
            }
        }else {
            log.debug("토큰 없음 - permitAll() 경로로 처리. URI: {}",request.getRequestURI());
        }
        // 토큰 벨리데이션
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

        // Swagger UI 경로
        if (matcher.match("/swagger-ui/**", path)) return true;
        if (matcher.match("/v3/api-docs/**", path)) {
            log.info("✅ Swagger API docs 경로 필터 제외: {}", path);
            return true;
        }
        if (matcher.match("/swagger-ui.html", path)) return true;
        if (matcher.match("/swagger-resources/**", path)) return true;
        if (matcher.match("/webjars/**", path)) return true;

        boolean shouldNotFilter = Arrays.stream(exceptURI.split(","))
                .anyMatch(pattern -> matcher.match(pattern, path));

        if (!shouldNotFilter) {
            log.debug("JWT 필터 적용 경로: {} [{}]", path, method);
        }

        return shouldNotFilter;
    }

}
