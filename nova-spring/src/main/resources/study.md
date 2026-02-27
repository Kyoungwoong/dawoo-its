# Spring 과제 준비 체크리스트 (내 정리)

---

## 1) 내가 고려해야 할 부분

1. **에러 정의**
    1) 가능하다면 `ErrorCode`를 **enum**으로 관리
    2) `GlobalExceptionHandler`로 예외를 **한 곳에서 통일 처리**

2. **에러 체크**
    1) `ReferenceType`과 `input`에 대해 **null 체크** → 에러 처리

3. **로깅**
    1) `slf4j` 사용 (`@Slf4j` 또는 `LoggerFactory`)

4. **패키지 구조**
    - 책임 단위로 분리(Controller/Service/Repository/DTO/Error/Config 등)

5. **공통 DTO 정리**
    - 요청/응답 DTO 분리
    - 공통 에러 응답 DTO 통일

---

## 2) 정리해야 할 부분

### 2-1) Controller

1. `@Controller` vs `@RestController` 정리
2. 요청 파라미터 관련 어노테이션 정리
    - `@RequestParam`
    - `@RequestBody`
    - `@RequestAttribute`
    - `@PathVariable`
3. 라우팅 어노테이션
    - `@GetMapping`
    - `@PostMapping`
4. Validation
    - `@Valid`
    - `@NotNull`, `@NotBlank`, `@Min`, `@Pattern`
5. 응답 제어
    - `ResponseEntity` (status / header / body)

---

### 2-2) Service / Repository / AOP

- `@Service`
- `@Repository`

#### AOP / 전역 예외 처리
- `@ControllerAdvice`
- `@ExceptionHandler(Exception.class)`

자주 다루게 되는 예외들:
- `MethodArgumentNotValidException` : `@Valid` DTO 검증 실패
- `BindException` / `ConstraintViolationException` : `@RequestParam` / `@PathVariable` 검증 실패
- `MissingServletRequestParameterException` : 필수 query param 누락
- `HttpMessageNotReadableException` : JSON 파싱 실패 (`@RequestBody` 깨짐)
- `HttpRequestMethodNotSupportedException` : 405 (메서드 불일치)
- `NoHandlerFoundException` : 404 (설정 필요할 수 있음)

---

### 2-3) 파일 읽기

1. `ClassPathResource` vs `Files.readString(Paths.get(...))`
2. 인코딩 UTF-8 고정
3. “상대경로/실행경로” 이슈 대응 (프로그래머스 환경에서 특히 함정)
4. Jackson 직렬화 컨트롤(응답 키/포맷 고정)
    - `@JsonProperty` 등
5. `@PostConstruct`로 한 번 로딩 → 파일 읽기 캐싱

---

### 2-4) 상태코드 기준 정리

- 성공: 200
- 잘못된 요청: 400
- 데이터 없음: 404
- 메서드 불일치: 405
- 서버 내부 오류: 500

(과제 스펙에 맞춰 응답 바디/에러 포맷도 함께 통일할 것)

---

    /**
     * 내가 고려해야할 부분
     * 1. 에러 정의
     *   1. 가능하다면 ErrorCode enum 처리
     *   2. GlobalException 처리
     * 2. 에러 체크
     *   1. ReferenceType과 input에 대해서 null체크 -> 에러
     * 3. 로깅
     *   1. slf4j (@Slf4j 또는 LoggerFactory)
     * 4. 패키지 구조
     * 5. 공통 DTO 정리
     * 6. 테스트코드
     */

    /**
     * 정리해야할 부분
     * @Contoller
     * 1. @Controller, @RestController 정리
     * 2. @RequestParam, @RequestBody, @RequestAttribute, @PathVariable
     * 3. @GetMapping/@PostMapping
     * 4. @Valid
     *   1. @NotNull/@NotBlank/@Min/@Pattern
     * 5. ResponseEntity
     *
     *
     *
     *
     *
     *
     * @Service
     * @Repository
     * @AOP
     * 1. @ControllerAdvice
     *   1. @ExceptionHandler(Exception.class)
     *   MethodArgumentNotValidException : @Valid DTO 검증 실패
     *   BindException / ConstraintViolationException : @RequestParam/@PathVariable 검증 실패
     *   MissingServletRequestParameterException : 필수 query param 누락
     *   HttpMessageNotReadableException : JSON 파싱 실패(@RequestBody 깨짐)
     *   HttpRequestMethodNotSupportedException : 405
     *   NoHandlerFoundException : 404 (설정 필요할 때 있음)
     * @파일 읽기
     * 1. ClassPathResource vs Files.readString(Paths.get(...))
     * 2. 인코딩 UTF-8 고정
     * 3. “상대경로/실행경로” 이슈 대응 (프로그래머스 환경에서 특히 함정)
     * 4. Jackson 직렬화 컨트롤(응답 키/포맷 고정) (@JsonProperty 등)
     * 5. @PostConstruct로 한 번 로딩 => 파일 읽기에서 캐싱
     *
     * @상태코드 기준 정리
     */