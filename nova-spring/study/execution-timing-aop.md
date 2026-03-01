# Execution Timing AOP (Service Only)

이 문서는 `nova-spring`에서 적용한 “메소드 실행시간 측정 AOP”의 목적/적용 범위/로그 형태를 정리합니다.

## 목적

- 과제 환경에서 "너무 복잡하지 않게" 실행시간을 측정
- 실무 감각으로는 "중복 로깅 최소화" + "핵심 구간만 측정"을 지향

---

## 적용 범위(현재 기준)

- 대상: `@Service`가 붙은 클래스의 `public` 메소드
- 제외: 컨트롤러/필터/인터셉터 등 HTTP 계층은 측정하지 않음

왜 Service만?

- 과제에서 가장 중요한 로직이 Service에 있고
- 컨트롤러까지 다 찍으면 로그가 과도하게 많아지기 쉬움

---

## 관련 파일

- Aspect: `src/main/java/com/example/spring/common/aop/ExecutionTimingAspect.java`
- 의존성: `build.gradle`에 `org.springframework.boot:spring-boot-starter-aop`

---

## 동작 방식

- `@Around`로 메소드 실행 전/후를 감싸서 시간 측정
- 시간 측정은 `System.nanoTime()` 사용
- 로그에는 아래를 출력
  - `tookMs`: 밀리초(ms)
  - `tookUs`: 마이크로초(us)
  - `target`: 클래스/메소드

로그 예시

- `tookMs=12 tookUs=12345 target=TransitService.getCardTripCounts`

---

## 설정(옵션)

`ExecutionTimingAspect`는 아래 설정 값을 읽습니다.

- `app.timing.threshold-ms`
  - 기본값: `0`
  - 의미: 이 값 이상일 때만 INFO 로그로 남김
  - 과제에서는 0으로 두면 항상 찍혀서 확인이 쉬움

설정 예시(`application.yml`):

```yaml
app:
  timing:
    threshold-ms: 5
```

---

## 과제/실무 사이의 타협 포인트

- 과제: `threshold-ms=0` (항상 찍기)
- 실무: `threshold-ms`를 5~20ms 정도로 올리고, 느린 메소드만 INFO로 관찰

---

## 주의사항

- AOP는 Spring Bean에만 적용됨
  - `new TransitService()` 같은 직접 생성 객체에는 적용되지 않음
- 같은 클래스 내부에서 자기 메소드를 호출하는 경우(self-invocation)는 프록시를 타지 않아 AOP가 안 걸릴 수 있음
