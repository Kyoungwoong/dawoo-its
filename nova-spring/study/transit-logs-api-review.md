# API Review: GET `/api/transit/logs` (nova-spring)

이 문서는 `Question.md`의 2번째 API(`/api/transit/logs`) 구현을 기준으로, 현재 코드의 상태를 평가하고 개선 포인트를 정리합니다.

## 0) 스펙 요약(Question.md 기준)

- Query
  - `cardId` 선택
  - `status` 선택: `COMPLETED|IN_PROGRESS|CANCELED`
  - `from` 선택: ISO-8601 UTC inclusive
  - `to` 선택: ISO-8601 UTC exclusive
  - `limit` 선택: 1~100, 기본 20
- 반환
  - 조건을 모두 만족하는 로그
  - `timestamp` 내림차순(최신 먼저)
  - `limit`만큼만
  - `fare`는 반드시 number
- 에러
  - 잘못된 status/from/to/limit이면 400

---

## 1) 현재 구현 위치

- Controller
  - `src/main/java/com/example/spring/controller/TransitController.java`
  - `@GetMapping("/logs")`
- Service
  - `src/main/java/com/example/spring/service/TransitService.java`
  - `getLogs(LogRequest)`
- Request/Response DTO
  - `src/main/java/com/example/spring/dto/LogRequest.java`
  - `src/main/java/com/example/spring/dto/LogResponse.java`
- 입력 검증
  - `src/main/java/com/example/spring/common/Validator.java`

---

## 2) 잘한 점

- Controller는 파라미터를 DTO(`LogRequest`)로 모아서 Service로 넘김
  - 파라미터가 늘어날 때 컨트롤러 시그니처가 덜 복잡해짐
- `fare`가 number/string 혼합이어도 응답에서 number로 맞추려는 시도
  - `LogResponse.createLogResponse()`에서 `Integer.parseInt(String.valueOf(fare))`
- `status` 허용값 검증을 별도 Validator로 분리

---

## 3) 개선/주의 포인트 (중요)

### 1) `from/to` 검증은 "문자 비교"가 아니라 "파싱"이 맞음

- 이전 방식처럼 문자열이 특정 값("ISO-8601")인지 비교하면 실제 데이터 검증이 되지 않음
- ISO-8601은 `Instant.parse(...)`로 검증/파싱하는 게 안전함

### 2) `limit` 적용은 정렬 이후에 해야 스펙과 일치

- 정렬 전 `limit`을 먼저 채우고 break 하면,
  - 최신 로그가 뒤에 있어도 결과에서 빠질 수 있음
- 해결: filter -> sort(timestamp desc) -> limit 순서로 처리

### 3) timestamp 정렬은 문자열 비교보다 시간 비교가 안전

- ISO-8601 문자열은 보통 문자열 정렬이 가능하지만,
  - 스펙에 "시간 비교 권장"이 있고
  - parsing을 이미 하므로 `Instant` 비교로 통일하는 게 명확함

### 4) 캐시 로딩 실패(transitList==null) 케이스 처리

- 앱 시작 시 파일을 1회 로딩하는 구조라면
  - 로딩 실패 시 API가 NPE로 터지지 않게
  - 명시적으로 에러 응답을 내려야 함

---

## 4) 이번에 반영한 수정 사항

- `Validator`가 `from/to`를 `Instant.parse(...)`로 검증하도록 수정
  - 파일: `src/main/java/com/example/spring/common/Validator.java`
- `TransitService.getLogs`에서
  - 로딩 실패 시 `DawooException(FILE_CONTENTS_NOT_AVAILABLE)` 던짐
  - `from/to`를 `Instant`로 파싱해서 필터링 적용
  - 정렬 후 `limit` 적용
  - 파일: `src/main/java/com/example/spring/service/TransitService.java`

---

## 5) 남아있는(선택) 개선

- 스펙은 `/api/transit/logs`가 JSON Array를 반환하도록 되어 있는데,
  - 현재는 `ResponseDto` wrapper로 감싸서 반환 중
  - 과제 채점이 스펙 엄격이면, 이 부분은 스펙에 맞추는 게 필요
- `fare`가 숫자가 아닌 값이면 `NumberFormatException`이 날 수 있음
  - 데이터 오류로 간주할지, 400/500 정책을 정하면 좋음
