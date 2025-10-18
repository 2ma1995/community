package mini.community.global.config;

import lombok.RequiredArgsConstructor;
import mini.community.global.filter.ExceptionHandlerFilter;
import mini.community.global.filter.JwtTokenFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@Order(300)
public class WebSecurityConfig {
    private final JwtTokenFilter jwtTokenFilter;
    private final ExceptionHandlerFilter exceptionHandlerFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // 세션 / CSRF 비활성화
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                // 인가 정책 정의
                .authorizeHttpRequests(auth -> auth
                                .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/swagger-ui.html", "/swagger-resources/**", "/webjars/**").permitAll()
                                //회원가입 / 로그인 / 공개 프로필 API허용
                                .requestMatchers(HttpMethod.POST, "/api/users/register").permitAll()
                                .requestMatchers(HttpMethod.POST, "/api/auth/login").permitAll()
                                .requestMatchers(HttpMethod.GET, "/api/profiles").permitAll()
                                .requestMatchers(HttpMethod.GET, "/api/profiles/user/**").permitAll()

//                        .requestMatchers(HttpMethod.GET, "/api/**").permitAll()
//                        .requestMatchers(HttpMethod.POST, "/api/**").permitAll()
//                        .requestMatchers(HttpMethod.PUT, "/api/**").permitAll()
//                        .requestMatchers(HttpMethod.DELETE, "/api/**").permitAll()
//                        .requestMatchers("/ping").permitAll()
                                .anyRequest().authenticated()
                )
                // 필터 체인 구성
                .addFilterBefore(exceptionHandlerFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(jwtTokenFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(Arrays.asList(
                "http://localhost:3000", "http://localhost:8080")); // local?
        config.setAllowCredentials(true);
        config.addAllowedMethod("*"); // 모든 메서드 적용
        config.addAllowedHeader("*");
        config.setExposedHeaders(Arrays.asList("Authorization", "x-auth-token"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
