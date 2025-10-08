package mini.community.global.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@Configuration
@EnableJpaAuditing
public class JpaAuditConfig {
    //엔티티가 생성/수정될 때 자동으로 시간 정보를 기록
}
