package com.raillylinker.module_api_service_v1;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import com.raillylinker.module_api_service_v1.const_objects.ProjectConfigs;

import java.util.TimeZone;

@ComponentScan(
        basePackages = {
                // Bean 스캔할 모듈 패키지 리스트
                "com.raillylinker.module_api_service_v1",
                "com.raillylinker.module_idp_common",
                "com.raillylinker.module_idp_jpa"
        }
)
@EntityScan("com.raillylinker.module_idp_jpa.jpa.entities")
@EnableJpaRepositories("com.raillylinker.module_idp_jpa.jpa.repositories")
@SpringBootApplication
public class ApplicationMain {

    @Bean
    public CommandLineRunner init() {
        return args -> {
            // 서버 타임존 명시적 설정
            TimeZone.setDefault(TimeZone.getTimeZone(ProjectConfigs.SYSTEM_TIME_ZONE));
//            System.out.println("Current TimeZone: " + TimeZone.getDefault().getID());
        };
    }

    public static void main(String[] args) {
        SpringApplication.run(ApplicationMain.class, args);
    }
}