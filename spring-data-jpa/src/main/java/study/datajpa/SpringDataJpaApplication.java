package study.datajpa;

import java.util.Optional;
import java.util.UUID;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing //MappedSuperclass 사용 시 필수
@SpringBootApplication
public class SpringDataJpaApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringDataJpaApplication.class, args);
    }

    //@CreateBy 또는 @LastModifiedBy 가 호출 될 때 메서드를 호출하여 값을 얻어가서 설정
    @Bean
    public AuditorAware<String> auditorProvider() {
        return () ->  Optional.of(UUID.randomUUID().toString());
    }
}
