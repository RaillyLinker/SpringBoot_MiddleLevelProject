package com.raillylinker.module_api_service_v1.const_objects;

// [프로젝트 전역 상수 모음]
// 아래 변수들은 절대 런타임에 변경되어서는 안됩니다.
// 왜냐면, 서버 복제와 같은 Scale out 기법을 사용시 메모리에 저장되는 상태변수가 존재하면 에러가 날 것이기 때문입니다.
// 꼭 메모리에 저장을 해야한다면 Redis, Kafka 등을 사용해 결합성을 낮추는 방향으로 설계하세요.
public class ProjectConfigs {
    // !!!현 프로젝트에서 사용할 타임존 설정 (UTC, Asia/Seoul, ...)!!!
    public static final String SYSTEM_TIME_ZONE = "Asia/Seoul";

    // 서버 내에서 사용할 모든 정보 암호화에 사용될 AES256 IV 16자
    public static final String SERVER_SECRET_IV = "odkejduc726dj48d";
    // 서버 내에서 사용할 모든 정보 암호화에 사용될 AES256 SecretKey 32자
    public static final String SERVER_SECRET_SECRET_KEY = "8fu3jd0ciiu3384hfucy36dye9sjv7b3";
}
