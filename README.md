# 미들급 백엔드 개발자 프로젝트 (Springboot)

## 프로젝트 설명
본 프로젝트는 멀티 모듈 구조의 미들급 백엔드 개발자 수준 프로젝트입니다.<br>
모듈 분리의 기준은 독립적 기능 중심 분리에서 시작하여 서비스 구현으로 나뉘어집니다.<br>

## 프로젝트 설명 상세
### 구현 기능
본 프로젝트에서 구현한 기능은 두가지입니다.<br>
1. 프리랜서 프로필 조회 서비스 구현
2. 포인트 충전 서비스 구현

### 구현 기능 상세
#### [프리랜서 프로필 조회 서비스]
1. 등록된 프리렌서의 프로필 목록을 조회하는 API<br>
검색 결과에서 Sorting(이름 가나다 순, 조회 수 순, 등록 최신 순) 기능을 지원하며,<br>
검색 리스트는 Pagination 형태,<br>
조회시 각 프로필은 "프리랜서의 이름, 프로필 상세 조회 수, 프로필 등록 날짜"가 포함되어야 합니다.
2. 프리렌서 프로필 상세 조회 수 업데이트 API<br>
프로필 상세를 조회할 경우 호출되는 '조회수 업데이트 API' 구현<br>
업데이트된 조회수는 프리랜서 목록 조회 API 결과값에 반영되어야 하며,<br>
조회수가 즉시 반영될 필요가 없지만 대량의 트래픽을 고려하여 구현해야 합니다.

#### [포인트 충전 서비스]
1. 결제를 통해 자사서비스의 포인트를 충전하는 서비스를 구현합니다.<br>
토스페이먼츠를 기준으로 결제 서비스를 구현하며,<br>
클라이언트 API 가 완성되어있다고 가정하고 서버 API 를 구현합니다.<br>
포인트 적립 비율은 원화와 1:1(ex : 1만원 결제시 1만 포인트 적립)이며,<br>
인증/인가 구현은 생략하여도 무관하며, 앞선 과제에서 구현한 프리랜서가 포인트를 충전한다고 가정합니다.<br>
추후 다른 PG 사로 대체, 추가 할 수 있도록 유연하게 구현하고,<br>
추가 포인트 적립 이벤트 등의 다양한 가격 정책이 추가될 것을 가정하며, 장애 발생을 고려한 예외처리를 구현합니다.

### 개발 조건
1. 개발 언어는 Java, <br>
프레임워크는 Springboot, <br>
데이터베이스 라이브러리는 JPA 및 QueryDSL 을 사용합니다.
2. 빌드는 Gradle 기반입니다.
3. 데이터베이스 및 미들웨어, 그외 사용 라이브러리는,<br>
MariaDB<br>
를 사용하였습니다.

### 개발 중점
1. 객체지향적 설계
2. 예외처리를 통한 안정성 확보
3. 대량 트래픽 및 동시성 고려

### 구현 강점
1. 멀티 모듈 사용
2. 테스트 코드 작성
3. 포인트 결제 시스템에서 할인 쿠폰 기능 구현
