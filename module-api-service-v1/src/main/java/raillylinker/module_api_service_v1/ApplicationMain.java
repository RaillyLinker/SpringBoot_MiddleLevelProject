package raillylinker.module_api_service_v1;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import raillylinker.module_api_service_v1.datasources.memory_const_object.ProjectConst;

import java.util.TimeZone;

@ComponentScan(
        basePackages = {
                // Bean 스캔할 모듈 패키지 리스트
                "raillylinker.module_api_service_v1",
                "raillylinker.module_idp_common"
        }
)
@SpringBootApplication
public class ApplicationMain {

    @Bean
    public CommandLineRunner init() {
        return args -> {
            // 서버 타임존 명시적 설정
            TimeZone.setDefault(TimeZone.getTimeZone(ProjectConst.SYSTEM_TIME_ZONE));
//            System.out.println("Current TimeZone: " + TimeZone.getDefault().getID());
        };
    }

    public static void main(String[] args) {
        SpringApplication.run(ApplicationMain.class, args);
    }
}