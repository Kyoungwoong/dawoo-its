# ControllerAdvice Standard (nova-spring)

이 문서는 `nova-spring` 프로젝트에서 적용한 `@RestControllerAdvice` 기반 예외 처리 구조를 정리합니다.

## 목적

- 컨트롤러/서비스에서 발생한 예외를 한 곳에서 처리
- 에러 응답 포맷을 고정(표준화)
- 로깅을 중앙화(중복 로깅/누락 방지)

---

## 1) 전체 구조 요약

- 예외 던짐
  - 서비스/컨트롤러에서 비정상 상황을 `DawooException`으로 던짐
- 공통 처리
  - `ExceptionController`(`@RestControllerAdvice`)가 예외를 잡아 표준 응답으로 변환
- 표준 응답
  - `ErrorResponse`에 `status/message/data/timeStamp`를 담아 JSON으로 반환
- 에러 코드
  - `ErrorCode`에 HTTP 상태와 기본 메시지를 정의

---

## 2) 관련 파일과 역할

### `common.exception/ExceptionController`

- 파일: `src/main/java/com/example/spring/common/exception/ExceptionController.java`
- 역할
  - `@ExceptionHandler(DawooException.class)`:
    - 우리가 의도적으로 만든 도메인 예외를 표준 응답으로 변환
    - 장애 추적을 위해 로그도 남김
  - `@ExceptionHandler(Exception.class)`:
    - 나머지 모든 예외를 잡아서 500 표준 응답으로 변환(안전망)

핵심 포인트

- `DawooException` 케이스도 로그를 남김(handled)
- "Unhandled"은 stacktrace 포함해서 `log.error`로 남김

---

### `common.exception/DawooException`

- 파일: `src/main/java/com/example/spring/common/exception/DawooException.java`
- 역할
  - `ErrorCode`를 들고 있는 RuntimeException
  - 필요한 경우 `errorMap`(추가 데이터)와 커스텀 메시지를 포함 가능

지원 생성자

- `new DawooException(errorCode)`
- `new DawooException(errorCode, message)`
- `new DawooException(errorCode, errorMap)`
- `new DawooException(errorCode, message, errorMap)`

---

### `domain/ErrorCode`

- 파일: `src/main/java/com/example/spring/domain/ErrorCode.java`
- 역할
  - 표준 에러의 HTTP 상태(`HttpStatus`)와 기본 메시지를 정의

현재 예시

- `INTERNAL_SERVER_ERROR(500, "API Server Error")`

(과제 기준으로는 최소 개수로 시작하고, 필요할 때만 늘리는 게 좋음)

---

### `dto/ErrorResponse`

- 파일: `src/main/java/com/example/spring/dto/ErrorResponse.java`
- 역할
  - 표준 에러 응답 포맷

필드

- `timeStamp`: 에러 발생 시각(인스턴스 생성 시점)
- `status`: HTTP 상태 코드 정수(예: 500)
- `message`: 에러 메시지
- `data`: 추가 데이터(Map 등)

생성

- `ErrorResponse.createErrorResponse(errorCode, data)`

---

## 3) 적용 예시

### 서비스에서 도메인 예외 던지기

예: transit 로그가 로딩되지 않은 경우

```java
throw new DawooException(
    ErrorCode.INTERNAL_SERVER_ERROR,
    "Transit logs are not loaded",
    Map.of("path", TRANSIT_ARRAY_PATH)
);
```

이 예외는 `ExceptionController`의 `@ExceptionHandler(DawooException.class)`에 의해 처리됨.

---

## 4) 결과 응답 형태(예)

```json
{
  "timeStamp": "2026-03-01T16:08:23.123",
  "status": 500,
  "message": "API Server Error",
  "data": {
    "path": "transit_logs_array.json"
  }
}
```

---

## 5) 운영 규칙(과제/실무 중간 지점)

- 컨트롤러에서 try/catch로 에러 응답을 직접 만들지 않기
- 예상 가능한 에러는 `DawooException(ErrorCode + data)`로 던지기
- `ExceptionController`에서 로그는
  - `DawooException`: stacktrace 없이도 충분(필요하면 추가)
  - Unhandled Exception: stacktrace 포함

---

## 6) 다음 개선(선택)

- `ErrorCode`에 `code` 문자열 필드 추가(클라이언트가 안정적으로 분기 가능)
- Validation 에러(`MethodArgumentNotValidException`) 전용 핸들러 추가
- `traceId`(MDC) 연결해서 요청 단위 추적 강화
