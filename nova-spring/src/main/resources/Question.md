# Spring 과제 연습 문제 (완전 신규): TransitLog API (Java 11)

---

## 0) 제약 조건

- Java 11 (record 사용 금지)
- Spring Boot + Jackson(ObjectMapper)
- 파일 경로: `./data/input/transit_logs.json`
- 앱 시작 시 파일을 **1회만 로딩해서 캐시**할 것 (`@PostConstruct`)
- 모든 응답은 JSON (health 제외 가능)
- **요구 스펙(키 이름/타입/정렬/상태코드)을 엄격히 준수**

---

## 1) 데이터 파일 형식

파일: `./data/input/transit_logs.json` (JSON Array)

각 원소는 “교통카드 탑승 기록”을 나타낸다.

### 필드
- `logId` (string) : 고유 ID
- `cardId` (string) : 카드 ID (예: "CARD-1001")
- `stationIn` (string) : 승차역
- `stationOut` (string|null) : 하차역 (미하차면 null)
- `fare` (number|string) : 요금 (정수). 문자열로 들어올 수도 있음. 예: 1250 또는 "1250"
- `timestamp` (string) : ISO-8601 UTC. 예: "2026-02-27T09:10:00Z"
- `status` (string) : "COMPLETED" | "IN_PROGRESS" | "CANCELED" (그 외 값은 입력 데이터 오류로 취급)

### 데이터 주의사항
> 주의:
> 1) 동일한 `cardId`가 여러 번 등장한다.
> 2) `fare`는 number 또는 string 형태로 섞여 들어올 수 있다.
> 3) `stationOut`이 null이면 **미하차** 상태이며, `status`는 반드시 "IN_PROGRESS"여야 한다. (어기면 데이터 오류)
> 4) `timestamp`는 정렬/필터링에 사용된다(문자열 비교가 아닌 시간 비교 권장).

### 예시(일부)
```json
[
  {
    "logId": "L001",
    "cardId": "CARD-1001",
    "stationIn": "Gangnam",
    "stationOut": "Yeoksam",
    "fare": 1250,
    "timestamp": "2026-02-27T09:10:00Z",
    "status": "COMPLETED"
  },
  {
    "logId": "L002",
    "cardId": "CARD-1001",
    "stationIn": "Yeoksam",
    "stationOut": null,
    "fare": "1250",
    "timestamp": "2026-02-27T10:15:00Z",
    "status": "IN_PROGRESS"
  },
  {
    "logId": "L003",
    "cardId": "CARD-2002",
    "stationIn": "Hongdae",
    "stationOut": "Sinchon",
    "fare": 1400,
    "timestamp": "2026-02-26T20:00:00Z",
    "status": "COMPLETED"
  }
]
```

## 2) 구현해야 할 API

### 2-1) GET `/api/transit/cards`

**설명**: 데이터에 등장하는 카드 목록을 반환한다.

**요구사항**
- 반환 형태: JSON Array
- 각 원소는 다음 키를 가진다.
    - `cardId` (string)
    - `totalTrips` (number) : 해당 cardId의 전체 로그 개수
- 정렬:
    - `totalTrips` 내림차순
    - 같으면 `cardId` 오름차순
- 예: `CARD-1001`이 2개 로그면 totalTrips=2

**응답 예시**
```json
[
  { "cardId": "CARD-1001", "totalTrips": 2 },
  { "cardId": "CARD-2002", "totalTrips": 1 }
]
```

서버 내부 오류: 500

---

### 2-2) GET `/api/transit/logs`

**설명**: 조건에 따라 로그를 조회한다.

**Query Parameter**
- `cardId` (선택)
- `status` (선택) : "COMPLETED" | "IN_PROGRESS" | "CANCELED"
- `from` (선택) : ISO-8601 UTC (inclusive)
- `to` (선택) : ISO-8601 UTC (exclusive)
- `limit` (선택) : 1~100, 기본 20

**요구사항**
- cardId/status/from/to 조건을 모두 만족하는 로그만 반환
- 반환 형태: JSON Array
- 각 원소는 다음 키를 가진다.
    - `logId` (string)
    - `cardId` (string)
    - `stationIn` (string)
    - `stationOut` (string|null)
    - `fare` (number)  ← 반드시 number로 반환
    - `timestamp` (string)
    - `status` (string)
- 정렬: `timestamp` 내림차순 (최신이 먼저)
- `limit`만큼만 반환

**에러 처리**
- `status`가 허용 값이 아니면:
    - 400, `{ "error": "bad request" }`
- `from/to`가 ISO-8601이 아니면:
    - 400, `{ "error": "bad request" }`
- `limit`가 범위를 벗어나면:
    - 400, `{ "error": "bad request" }`

---

### 2-3) GET `/api/transit/stats?cardId={id}`

**설명**: 특정 카드의 통계 정보를 반환한다.

**Query Parameter**
- `cardId` (필수)

**요구사항**
- 반환 형태: JSON Object
- 키:
    - `cardId` (string)
    - `completedTrips` (number) : status가 COMPLETED인 개수
    - `inProgressTrips` (number) : status가 IN_PROGRESS인 개수
    - `canceledTrips` (number) : status가 CANCELED인 개수
    - `totalFareCompleted` (number) : COMPLETED에 대해 fare 합계
    - `mostUsedStationIn` (string) : stationIn 최빈값
        - 동률이면 사전순(오름차순)으로 가장 앞선 역
- 해당 cardId 데이터가 없으면:
    - 404, `{ "error": "data not found" }`

**응답 예시**
```json
{
  "cardId": "CARD-1001",
  "completedTrips": 1,
  "inProgressTrips": 1,
  "canceledTrips": 0,
  "totalFareCompleted": 1250,
  "mostUsedStationIn": "Yeoksam"
}
```

---

### 2-4) GET `/health`
* 응답: `"OK"`
* 상태코드: 200



