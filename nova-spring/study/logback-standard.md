# Logback XML Standard (Spring)

이 문서는 `logback.xml` / `logback-spring.xml`에서 자주 쓰는 양식(패턴)과, 과제/실무에서 최소로 표준화할 항목을 정리합니다.

## 1) 어떤 파일을 쓰나

- `logback-spring.xml`
- Spring Boot가 먼저 읽고, Spring 기능(프로파일, property 치환 등)과 더 잘 맞습니다.
- 특별한 이유가 없으면 `logback-spring.xml`을 권장합니다.

- `logback.xml`
- 순수 Logback 설정.
- Spring의 property/profile 연동이 제한적입니다.

## 2) 최소 표준 패턴(권장)

- 목적: 한 줄에서 시간/레벨/스레드/요청 상관관계(traceId)/로거/메시지를 고정.
- 파일: `src/main/resources/logback-spring.xml`

예시 패턴:

- `%d{yyyy-MM-dd HH:mm:ss.SSS} %-5level [%thread] traceId=%X{traceId:-} %logger{36} - %msg%n`

이 패턴에 포함된 항목:

- `%d{...}`: 타임스탬프
  - `%-5level`: 레벨(고정 폭)
- `[%thread]`: 스레드 이름
  - `traceId=%X{traceId:-}`: MDC에 `traceId`가 있으면 출력, 없으면 `-`
- `%logger{36}`: 로거 이름(패키지 포함, 길이 제한)
  - `%msg`: 메시지
  - `%n`: 줄바꿈

## 3) 자주 쓰는 패턴 토큰

- `%d{pattern}`
  - 예: `%d{HH:mm:ss.SSS}`

- `%level`
  - `%-5level` 처럼 폭을 맞추는 형태가 흔함

- `%thread`
  - 동시성 문제 추적에 도움

- `%logger{len}`
  - 로거(클래스) 식별. 너무 길면 잘라서 출력

- `%class` / `%method` / `%line`
  - 호출 위치를 찍지만 비용이 있고 노이즈가 큼
  - 과제에선 필요할 때만 임시로 켜는 편을 추천

- `%msg`
  - 로그 메시지

- `%ex` / `%stacktrace`
  - 예외 스택트레이스

- `%X{key}`
  - MDC 값 (요청 단위 상관관계 키)

## 4) MDC(traceId) 표준

- 표준 키: `traceId`
- 요청 단위로 하나만 있어도 과제/실무에서 효과가 큼
- `Filter`/`Interceptor`로 설정하는 게 정석이지만, 과제 제약이 있으면 컨트롤러 진입점에서만이라도 넣을 수 있음

## 5) 레벨 기준(간단 규칙)

- `INFO`: 요청 처리 결과 요약, 비즈니스 이벤트
- `WARN`: 잘못된 입력, 재시도 가능한 문제
- `ERROR`: 예외 발생, 요청 실패
- `DEBUG`: 상세 헤더/바디/중간 값 (제출용 과제면 기본 OFF 권장)

## 6) 설정 템플릿 예시

- 콘솔 표준 포맷 + INFO 루트

```xml
<configuration>
  <property name="CONSOLE_PATTERN" value="%d{yyyy-MM-dd HH:mm:ss.SSS} %-5level [%thread] traceId=%X{traceId:-} %logger{36} - %msg%n"/>

  <appender name="CONSOLE" class="ch.qos.logback.core.ConsoleAppender">
    <encoder>
      <pattern>${CONSOLE_PATTERN}</pattern>
    </encoder>
  </appender>

  <root level="INFO">
    <appender-ref ref="CONSOLE"/>
  </root>
</configuration>
```

## 7) 프로파일별 레벨(선택)

- 로컬에서만 DEBUG를 보고 싶으면 `springProfile`로 분기

```xml
<springProfile name="local">
  <root level="DEBUG">
    <appender-ref ref="CONSOLE"/>
  </root>
</springProfile>

<springProfile name="!local">
  <root level="INFO">
    <appender-ref ref="CONSOLE"/>
  </root>
</springProfile>
```

## 8) 실무에서 피하는 패턴

- 요청/응답 바디를 기본 INFO로 찍기
- 개인정보/토큰/세션 값 로그
- 모든 메소드 진입/탈출을 AOP로 무조건 찍기

