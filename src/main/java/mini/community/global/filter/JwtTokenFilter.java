package mini.community.global.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mini.community.global.context.TokenContextHolder;
import mini.community.global.token.TokenBlacklistService;
import mini.community.global.token.TokenManager;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtTokenFilter extends OncePerRequestFilter {

    private final TokenManager tokenManager;
    private final TokenBlacklistService tokenBlacklistService;
    private final static String AUTHORIZATION_HEADER = "x-auth-token";

    @Value("${except-uri:}")
    private String exceptURI;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException, NullPointerException {

        // 토큰 조회
        String uri = request.getRequestURI();
        String method = request.getMethod();
        String token = request.getHeader(AUTHORIZATION_HEADER);

        try {
            if (token != null && !token.isBlank()) {
                // redis 블랙리스트 확인
                if (tokenBlacklistService.isBlacklisted(token)) {
                    log.warn("블랙리스트 토큰 접근 차단: uri={}, tokenPrefix={}",
                            uri, token.substring(0, Math.min(10, token.length())));
                    sendUnauthorized(response, "Token is blacklisted (logged out)");
                    return;
                }

                // jwt유효성 검사(+컨텍스트 세팅은 TokenManager에서 수행)
                tokenManager.validateToken(token, "access");

                long userId = TokenContextHolder.getContext().getUserId();
                log.info("After validateToken - userId={}", userId);
                //  SecurityContext에 인증 정보 수동 등록 (403 방지 핵심!)
                if (userId > 0) {
                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(
                                    userId,
                                    null,
                                    List.of(new SimpleGrantedAuthority("ROLE_USER"))
                            );
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    log.debug("✅ SecurityContext 등록 완료 - userId={}", userId);
                } else {
                    log.warn("⚠️ userId가 0입니다. 토큰 파싱 로직 확인 필요.");
                }
                log.debug("JWT 통과: uri={} method={} userId={}", uri, method, userId);
            } else {
                log.trace("토큰 없음 for uri={} method={}", uri, method);
            }
            // 다음 필터 진행
            filterChain.doFilter(request, response);
        } finally {
            // 요청 종료후 쓰레드로컬 정리
            TokenContextHolder.clear();
            SecurityContextHolder.clearContext();
        }
    }

//    private String resolveToken(HttpServletRequest request) {
//        return request.getHeader(AUTHORIZATION_HEADER);
//    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getServletPath();
        String method = request.getMethod();
        AntPathMatcher matcher = new AntPathMatcher();
        // 필터 예외 경로
        //CORS preflight
        if ("OPTIONS".equalsIgnoreCase(method)) return true;
//        if ("/api/profiles".equals(path) && "GET".equalsIgnoreCase(method)) return true;
//        if ("/api/profiles/image".equals(path) && "POST".equalsIgnoreCase(method)) return true;
//        if (matcher.match("/api/profiles/user/**", path) && "GET".equalsIgnoreCase(method)) return true;
//        if ("/api/auth".equals(path) && "POST".equalsIgnoreCase(method)) return true;
//        if ("/api/users".equals(path) && "POST".equalsIgnoreCase(method)) return true;

        // Swagger UI 예외 경로
        if (matcher.match("/swagger-ui/**", path)) return true;
        if (matcher.match("/v3/api-docs/**", path)) return true;
        if (matcher.match("/swagger-ui.html", path)) return true;
        if (matcher.match("/swagger-resources/**", path)) return true;
        if (matcher.match("/webjars/**", path)) return true;

        // 인증 없이 허용할 경로들(필요에 맞게 추가/수정)
        if (matcher.match("/api/users/register", path) && "POST".equalsIgnoreCase(method)) return true;
        if (matcher.match("/api/auth/login", path) && "POST".equalsIgnoreCase(method)) return true;
        if (matcher.match("/api/profiles", path) && "GET".equalsIgnoreCase(method)) return true;
        if (matcher.match("/api/profiles/user/**", path) && "GET".equalsIgnoreCase(method)) return true;

        // 프로필 이미지 업로드 인증필요 -> 필터 적용해야함
        if (matcher.match("/api/profiles/image", path) && "POST".equalsIgnoreCase(method)) return false; // 업로드는 인증 필요

        // except-uri (yml에서 관리)
        if (exceptURI == null || exceptURI.isBlank()) {
            boolean except = Arrays.stream(exceptURI.split(","))
                    .anyMatch(pattern -> matcher.match(pattern.trim(), path));
            if (except) {
                log.debug("except-uri 필터 제외: {}", path);
                return true;
            }
        }
        return false;
    }

    // 블랙리스트 차단 응답 헬퍼
    private void sendUnauthorized(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write("{\"error\": \"" + message + "\"}");
    }
}
