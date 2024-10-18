# 포트폴리오 토이 프로젝트 (Springboot)

## 프로젝트 설명

본 프로젝트는 작성자의 개발 능력 검증용 토이 프로젝트입니다.<br>
<br>
Springboot 멀티 모듈 아키텍쳐를 사용하였으며,<br>
모듈 분리의 기준은 독립적 기능 중심 분리에서 시작하여 서비스 구현으로 나뉘어집니다.<br>
<br>
모듈은 idp, dpd, api 의 세가지 종류로 나누었습니다.<br>

1. 다른 모듈에 종속성이 없는 independent(idp) 모듈
2. 서버 API 컨트롤러 개발용 api(api) 모듈
3. 그 외의 종속성을 가진 dependent(dpd) 모듈

위와 같이 구분하여,<br>
모듈화의 최대 장점인 관심사의 분리와 결합성/의존성의 최소화를 이루어냈습니다.<br>
<br>

### 구성 모듈 설명

![화면 캡처 2024-10-18 090100](https://github.com/user-attachments/assets/6535f464-7bf7-4503-ae24-f880717c85ff)

1. module-api-service-v1 :<br>
   서비스 API 가 구현되어 모여있는 모듈 (main 버전 단위 분리)
2. module-idp-common :<br>
   다른 모듈에 종속성이 없는 유틸성 클래스를 모아둔 모듈
3. module-idp-jpa :<br>
   다른 모듈에 종속성이 없는 JPA Entity, Repository, QueryDSL 을 모아둔 모듈

### 프로젝트 구조 설명
![화면 캡처 2024-10-18 200236](https://github.com/user-attachments/assets/45d1fe13-44e7-45bb-b876-979a12c6d40c)

- by_product_files : 프로젝트 실행시 생성되는 모든 파일 분산물은 이곳에 저장됩니다.<br>
대표적으로 로그 파일이 있으며, .gitignore 에 설정됩니다.
- external_files : 임의로 추가한 외부 파일들을 모아둔 폴더입니다.<br>
본 프로젝트에서는 프로젝트를 실행시키기 위한 최소 환경을 간편하게 구성해주는 docker-compose 파일이 저장되어 있습니다.<br>
- module-* : 위 모듈 설명과 동일.

### 프로젝트 실행
1. external_files 의 mariadb 도커 컨테이너를 실행하거나, mariadb 설치
2. module-idp-jpa 의 src/main/java/raillylinker/module_idp_jpa/data_sources/entities/*<br>
를 참고하여 mariadb 의 데이터베이스 테이블 구조를 맞춰주기.<br>
   (module-idp-jpa 의 application.yml 파일에서 ddl-auto 설정을 사용하는 방식도 있습니다.)
3. 상기한 준비가 완료되었다면 module-api-service-v1 의 ApplicationMain 을 실행시켜줍니다.
4. 로컬 웹 브라우저에서 127.0.0.1:8080 주소로 서비스에 접근하여 웰컴 페이지로 진입이 가능하며,<br>
"API 문서로 이동" 버튼을 클릭하여 Swagger API 문서 페이지로 진입하여 API 확인 및 기능 테스트를 수행할 수 있습니다.

![화면 캡처 2024-10-18 200515](https://github.com/user-attachments/assets/e5e7c13b-d631-4550-9055-cc1d3c9c5317)
![화면 캡처 2024-10-18 200621](https://github.com/user-attachments/assets/9cdab8b4-b4a2-48b3-9b4f-d729c9147428)



## 프로젝트 설명 상세

### 개발 조건

개발 조건은 현 시점 백엔드 개발자 풀의 보편적인 기술들을 사용합니다.

1. 개발 언어는 Java, <br>
   빌드툴은 Gradle<br>
   프레임워크는 Springboot MVC, <br>
   데이터베이스 ORM 라이브러리는 JPA(QueryDSL 사용) 을 사용합니다.<br>
   <br>
2. 데이터베이스 외 미들웨어, 그외 사용 라이브러리는 따로 사용하지 않았고,<br>
   프로젝트 구조를 단순화 하여 가독성을 높이기 위하여 MariaDB 만을 사용하였습니다.<br>
   (실행시 external_files/dockers 위치의 mariadb docker 를 사용하여 간편하게 실행 환경 구축)

### 구현 기능

본 프로젝트에서 구현한 기능은 두가지입니다.<br>

1. 프리랜서 프로필 조회 서비스 구현 (Database ORM 활용 능력, 대용량 트래픽 처리 기본 능력)
2. 포인트 충전 서비스 구현 (실무 서비스 설계 능력 및 종합 개발 능력)

### 구현 기능 상세

#### [프리랜서 프로필 조회 서비스]

1. 등록된 프리렌서의 프로필 목록을 조회하는 API<br>
   검색 결과에서 Sorting(이름 가나다 순, 조회 수 순, 등록 최신 순) 기능을 지원하며,<br>
   검색 리스트는 Pagination 형태,<br>
   조회시 각 프로필은 "프리랜서의 이름, 프로필 상세 조회 수, 프로필 등록 날짜"가 포함되어야 합니다.<br>
   <br>
2. 프리렌서 프로필 상세 조회 수 업데이트 API<br>
   프로필 상세를 조회할 경우 호출되는 '조회수 업데이트 API' 구현<br>
   업데이트된 조회수는 프리랜서 목록 조회 API 결과값에 반영되어야 하며,<br>
   조회수가 즉시 반영될 필요가 없지만 대량의 트래픽을 고려하여 구현해야 합니다.

#### [포인트 충전 서비스]

1. 결제를 통해 자사서비스의 포인트를 충전하는 서비스를 구현합니다.<br>
   클라이언트 API 는 완성되어있으며, <br>
   인증/인가 구현은 생략하고, 앞서 구현한 프리랜서가 포인트를 충전한다고 가정하여 서버 API 를 구현합니다.
    - 토스페이먼츠 기준 결제 서비스 구현
    - 포인트 적립 비율은 원화와 1:1(ex : 1만원 결제시 1만 포인트 적립)<br>
    - 추후 다른 PG 사로 대체, 추가 할 수 있도록 유연하게 구현<br>
    - 장애 발생을 고려한 예외처리
    - 할인 쿠폰 기능을 구현하며,
      추가 포인트 적립 이벤트 등의 다양한 가격 정책이 추가될 것을 가정

### 개발 중점 사항

1. 객체지향적 설계 (적용) - 올바른 코딩 기본기를 갖추고 있는지
2. 예외처리를 통한 안정성 확보 (적용) - 꼼꼼한 업무 수행 능력과 자신이 다루는 기술을 이해하고 있는지
3. 대량 트래픽 및 동시성 고려 (적용) - 개발 능력에 실전성을 갖추고 있는지

### 추가 사항

1. 멀티 모듈 사용 (적용)
2. 테스트 코드 작성 (미적용)
