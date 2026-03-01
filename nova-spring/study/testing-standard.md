# Testing Standard (nova-spring)

이 문서는 과제/스터디 수준에서 "최소 비용으로 효과 큰" 테스트 구성을 정리합니다.

## 목표

- 컨트롤러는 HTTP 응답 형태(스키마)를 깨지 않게 보장
- 서비스는 핵심 로직(집계/정렬)을 빠르게 검증
- 무거운 통합 테스트(@SpringBootTest)는 최소화

---

## 1) 테스트 종류(추천 기준)

### 1) `@WebMvcTest` (Controller slice test)

- 목적: 컨트롤러의 endpoint, status, JSON 응답 형태를 빠르게 검증
- 장점
  - Spring MVC만 얇게 띄워서 빠름
  - DB/외부 의존성이 없어도 가능
- 핵심 개념
  - `MockMvc`: HTTP 요청을 흉내내는 테스트 도구
  - `@MockBean`: 컨트롤러가 의존하는 Service를 mock으로 교체
  - `jsonPath`: JSON 응답 검증

예시 파일

- `src/test/java/com/example/spring/controller/TransitControllerTest.java`

---

### 2) 순수 Unit Test (Service)

- 목적: 서비스 로직(집계/정렬/필터링)을 Spring 없이 검증
- 장점
  - 가장 빠르고 실패 원인 파악이 쉬움
- 핵심 개념
  - 테스트는 입력/출력을 명확히 준비하고 결과만 검증
  - 외부 IO(파일/네트워크)가 끼면 테스트가 불안정해지므로 피함

예시 파일

- `src/test/java/com/example/spring/service/TransitServiceTest.java`

여기서는 `TransitService` 내부 상태(`transitList`)를 reflection으로 주입해서
파일 로딩/`@PostConstruct` 영향 없이 핵심 로직만 테스트합니다.

---

## 2) 어떤 걸 테스트해야 하나(과제용 최소)

- Controller
  - 응답 status(HTTP 200)
  - 공통 응답 wrapper(`ResponseDto`)의 필드 존재/값
  - `data` 필드 구조(배열/객체)

- Service
  - 집계(count)
  - 정렬 규칙
  - 입력 데이터의 edge case(빈 cardId 무시 등)

---

## 3) 테스트 실행

- 전체 테스트

`./gradlew test`

---

## 4) 흔한 실수

- `@WebMvcTest`에서 `@Autowired` Bean을 그대로 쓰려고 함
  - 해결: 컨트롤러 의존성을 `@MockBean`으로 채움

- 실행시간 AOP/로깅이 테스트를 깨는 것처럼 보임
  - 대부분은 로그일 뿐이고, 테스트 실패 원인은 응답/직렬화/빈 주입 문제인 경우가 많음

- 서비스 테스트에서 파일 읽기를 포함
  - 외부 환경에 따라 실패(경로/리소스) → 로직 테스트는 데이터를 직접 넣어 검증하는 게 안전
