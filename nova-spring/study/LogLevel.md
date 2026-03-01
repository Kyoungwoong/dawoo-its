### INFO (항상 켜두고 봐도 되는 ‘업무 이벤트’)

- 서비스가 정상적으로 무엇을 했는지 “요약”이 남아야 하는 것
- 예시
    - 서버 기동 시 중요한 설정/의존성 로딩 완료: Loaded transit logs
      count=20
    - 배치/스케줄러 작업 시작/완료
    - API 요청 처리 결과 요약(전역 access log로): GET /api/transit/
      cards 200 tookMs=12
- 규칙
    - 건당 너무 많이 찍지 말기(요청마다 5줄 이상이면 운영에서 지옥)
    - 개인정보/민감정보(주민번호, 계좌번호 전체, 카드번호 전체)는 절
      대 찍지 말기

### DEBUG (개발/장애 분석용 ‘내부 상태’)

- “왜 그렇게 됐는지”를 파고들 때 필요한 디테일
- 예시
    - 분기 선택: reading from classpath, reading from filesystem
    - 집계 중간값: cardCount=123, firstTimestamp=...
    - 외부 호출 request/response 바디(민감정보 마스킹 전제)
- 규칙
    - 성능/용량에 민감할 수 있으니 기본은 꺼두는 게 정상
    - 리스트 전체/대용량 JSON을 그대로 찍지 말기(크기/샘플만)

### ERROR (조치가 필요한 실패, 보통 알람 대상)

- 요청을 실패시켰거나, 기능이 동작 불능이 된 경우
- 예시
    - 파일 로딩 실패로 서비스 기동 실패
    - DB/외부 API 호출 실패로 요청이 5xx로 끝남
    - “절대 일어나면 안 되는 상태” (불변식 위반)
- 규칙
    - log.error("...", e) 처럼 예외 스택트레이스 포함
    - 같은 에러를 여러 레이어에서 중복 로깅하지 말기(보통
      ControllerAdvice/전역 핸들러에서 1번)

### (참고) WARN

- 지금은 질문에 없었지만 실무에서 많이 씀
- “요청은 성공/처리 가능하지만 이상 징후”
    - 예: 입력값이 비정상이라 일부 데이터 스킵, fallback 적용, 재시
      도 발생 등

네 코드에 바로 적용하면 이렇게 생각하면 됨

- TransitController: 직접 INFO 찍기보단 전역 access log가 이상적. 컨
  트롤러에서 꼭 찍어야 하면 INFO로 “요약 1줄(건수/시간)”만.
- TransitService#setUp: 성공 시 INFO(몇 건 로딩), 실패 시 ERROR + 예
  외 포함.
- FileReader: “classpath/fileSystem 어디서 읽었는지”는 보통 DEBUG,
  “파일 없음/파싱 실패”는 ERROR.