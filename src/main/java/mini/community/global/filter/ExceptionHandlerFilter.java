package mini.community.global.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mini.community.global.exception.UsernameFromTokenException;
import mini.community.global.exception.handler.ErrorDetailResponse;
import mini.community.global.exception.handler.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;

@Slf4j
@Component
@RequiredArgsConstructor
public class ExceptionHandlerFilter extends OncePerRequestFilter {
    private final ObjectMapper objectMapper;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            filterChain.doFilter(request, response);
        } catch (UsernameFromTokenException ex) {
            log.info("UsernameFromTokenException handler filter");
            setErrorResponse(HttpStatus.FORBIDDEN, response, ex);
        } catch (RuntimeException ex) {
            log.info("RuntimeException exception handler filter");
            setErrorResponse(HttpStatus.FORBIDDEN, response, ex);
        } catch (Exception ex) {
            log.info("Exception exception handler filter");
            setErrorResponse(HttpStatus.FORBIDDEN, response, ex);
        }
    }

    // ✅ Swagger 경로 제외
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getServletPath();
        AntPathMatcher matcher = new AntPathMatcher();

        return matcher.match("/swagger-ui/**", path) ||
                matcher.match("/v3/api-docs/**", path) ||
                matcher.match("/swagger-ui.html", path) ||
                matcher.match("/swagger-resources/**", path) ||
                matcher.match("/webjars/**", path);
    }


    private void setErrorResponse(HttpStatus status, HttpServletResponse response, Throwable ex) {
        response.setStatus(status.value());
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        ErrorDetailResponse detailResponse = ErrorDetailResponse.builder().message(ex.getMessage()).build();
        ErrorResponse errorResponse = ErrorResponse.builder()
                .errors(Arrays.asList(detailResponse))
                .build();
        try{
            response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
        }catch (IOException e){
            e.printStackTrace();
        }
    }
}
