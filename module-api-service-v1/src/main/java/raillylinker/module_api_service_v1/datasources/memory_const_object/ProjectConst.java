package raillylinker.module_api_service_v1.datasources.memory_const_object;

// [프로젝트 전역 상수 모음]
// 아래 변수들은 절대 런타임에 변경되어서는 안됩니다.
// 왜냐면, 서버 복제와 같은 Scale out 기법을 사용시 메모리에 저장되는 상태변수가 존재하면 에러가 날 것이기 때문입니다.
// 꼭 메모리에 저장을 해야한다면 Redis, Kafka 등을 사용해 결합성을 낮추는 방향으로 설계하세요.
public class ProjectConst {
    // !!!현 프로젝트에서 사용할 타임존 설정 (UTC, Asia/Seoul, ...)!!!
    public static final String SYSTEM_TIME_ZONE = "UTC";
}
