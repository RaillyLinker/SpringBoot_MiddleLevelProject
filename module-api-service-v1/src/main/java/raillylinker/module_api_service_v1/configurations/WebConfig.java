package raillylinker.module_api_service_v1.configurations;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
@EnableWebMvc
public class WebConfig implements WebMvcConfigurer {

    // (Cors 설정)
    private final List<String> corsList;

    public WebConfig(@Value("${custom-config.cors-allow-list:}#{T(java.util.Collections).emptyList()}") List<String> corsList) {
        this.corsList = corsList;
    }

    // [Cors 설정]
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        // 아래 설정을 적용할 요청 경로 (ex: "/somePath/**", "/path")
        var allPathRegistry = registry.addMapping("/**");

        if (corsList.isEmpty()) {
            allPathRegistry.allowedOriginPatterns("*"); // 모든 요청을 허용하려면 allowedOrigins 를 지우고 이것을 사용
        } else {
            allPathRegistry.allowedOrigins(corsList.toArray(new String[0])); // 자원 공유를 허용 할 URL 리스트
        }

        allPathRegistry
                .allowedMethods(
                        HttpMethod.POST.name(), HttpMethod.GET.name(),
                        HttpMethod.PUT.name(), HttpMethod.DELETE.name(),
                        HttpMethod.OPTIONS.name()
                ) // 클라이언트에서 발신 가능한 메소드 (ex: "GET", "POST")
                .allowedHeaders("*"); // 클라이언트에서 발신 가능한 헤더 (ex: "name", "addr")
    }

    // [Spring static Resource 경로 설정]
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 실제 경로 addResourceLocations 를 addResourceHandler 로 처리하여,
        // static Resource 에 접근하려면, http://127.0.0.1:8080/images/1.png, http://127.0.0.1:8080/favicon.ico 와 같이 접근 가능
        registry
                .addResourceHandler("/**")
                .addResourceLocations("classpath:/static/");
    }
}