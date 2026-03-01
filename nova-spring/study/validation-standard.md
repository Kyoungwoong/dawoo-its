# Validation Standard: `@Valid` (nova-spring)

이 문서는 Spring MVC에서 "Request 입력 검증"을 표준화하는 방법을 정리합니다.

## 목표

- 잘못된 입력을 컨트롤러/서비스 로직에 들어오기 전에 차단
- 에러 응답 포맷을 `ExceptionController`에서 통일
- 과제 환경에서도 "최소 코드"로 안정성 확보

---

## 1) 의존성

- `build.gradle`에 아래가 있어야 동작합니다.

- `org.springframework.boot:spring-boot-starter-validation`

---

## 2) 어디에 `@Valid`를 붙이나

### 1) `@RequestBody` DTO 검증

- JSON body를 DTO로 받을 때
- DTO 필드에 제약 어노테이션을 붙이고, 컨트롤러 파라미터에 `@Valid`를 붙입니다.

예시:

```java
public class CreateUserRequest {
  @NotBlank
  private String name;

  @Min(0)
  private int age;

  // getters/setters
}

@PostMapping("/users")
public ResponseEntity<?> create(@Valid @RequestBody CreateUserRequest req) {
  ...
}
```

발생 예외:

- `MethodArgumentNotValidException`

---

### 2) `@RequestParam`/`@PathVariable` 검증

- 쿼리 파라미터나 path 변수에 제약을 걸고 싶을 때
- 이 경우 컨트롤러 클래스(또는 메소드)에 `@Validated`가 필요합니다.

예시:

```java
@Validated
@RestController
class UserController {
  @GetMapping("/users/{id}")
  public ResponseEntity<?> get(
      @PathVariable @Min(1) long id,
      @RequestParam @Size(min = 1, max = 50) String q
  ) {
    ...
  }
}
```

발생 예외:

- `ConstraintViolationException`

---

## 3) 자주 쓰는 제약 어노테이션

- `@NotNull`: null 금지
- `@NotBlank`: null/빈문자/공백 금지(String)
- `@NotEmpty`: 빈 컬렉션/문자열 금지
- `@Size(min, max)`: 길이/크기
- `@Min`, `@Max`: 숫자 범위
- `@Positive`, `@PositiveOrZero`
- `@Email`
- `@Pattern(regexp=...)`

---

## 4) 에러 응답 표준화(현재 프로젝트)

검증 실패는 `ExceptionController`에서 공통 응답으로 처리합니다.

- 파일: `src/main/java/com/example/spring/common/exception/ExceptionController.java`

### 1) `MethodArgumentNotValidException` (Body DTO)

- `ErrorCode.BAD_REQUEST(400)`
- `data`에 필드별 오류 리스트를 포함

예시 응답 형태:

```json
{
  "timeStamp": "...",
  "status": 400,
  "message": "Bad Request",
  "data": {
    "errorCount": 2,
    "errors": [
      { "field": "name", "rejectedValue": "", "message": "must not be blank" },
      { "field": "age", "rejectedValue": -1, "message": "must be greater than or equal to 0" }
    ]
  }
}
```

### 2) `ConstraintViolationException` (Param/Path)

- `ErrorCode.BAD_REQUEST(400)`
- `data.errors[]`에 `path/invalidValue/message` 포함

---

## 5) 과제에서의 권장 규칙

- 컨트롤러에서 입력 검증을 먼저 걸고, 서비스는 "정상 입력"을 가정
- 검증 실패는 `@RestControllerAdvice`에 맡기고 컨트롤러에서 try/catch 하지 않기
- 과제 범위에서는 "필수값 + 범위" 정도만 걸어도 충분

---

## 6) 흔한 실수

- `@RequestParam`/`@PathVariable` 검증이 안 됨
  - 원인: `@Validated` 누락
 
- DTO 필드에 제약을 걸었는데도 동작 안 함
  - 원인: `spring-boot-starter-validation` 의존성 누락
  - 원인: 컨트롤러 파라미터에 `@Valid` 누락

---

## 7) `@Validated` 정리

### `@Validated`가 필요한 이유

- `@RequestParam`, `@PathVariable` 같은 \"단일 파라미터\"에 제약 어노테이션을 붙였을 때 실제 검증 트리거가 필요합니다.
- Spring MVC에서 이 트리거 역할을 흔히 `@Validated`가 담당합니다.

### 어디에 붙이나

- 보통 컨트롤러 클래스에 붙입니다.
- 메소드 단위로도 가능하지만, 과제에서는 클래스에 한 번 붙이는 게 관리가 쉽습니다.

예시:

```java
@Validated
@RestController
class UserController {
  @GetMapping(\"/users/{id}\")
  public String get(
      @PathVariable @Min(1) long id,
      @RequestParam @Size(min = 1, max = 50) String q
  ) {
    return \"ok\";
  }
}
```

### 실패하면 어떤 예외가 나오나

- 보통 `ConstraintViolationException`이 발생합니다.
- 이 프로젝트에서는 `ExceptionController`에서 잡아 `ErrorCode.BAD_REQUEST(400)`으로 통일해서 응답합니다.

### `@Valid`와의 관계

- `@Valid`: DTO(객체) 검증에 주로 사용 (`@RequestBody` 등)
- `@Validated`: 파라미터 검증 트리거 + 그룹 검증 지원(필요할 때 확장 가능)
