# 실시간 통신을 위한 다양한 방법을 학습
## 목차

1. [Polling](#1-polling)
2. [Long Polling](#2-long-polling)
3. [Server-Sent Events](#3-server-sent-events)
4. [WebSocket](#4-websocket)
5. [STOMP on WebSocket](#5-stomp-on-websocket)

## 1. Polling

### 1.1. 정의
- 클라이언트가 서버에 주기적으로 요청을 보내는 방식

### 1.2. 특징

- 구현이 상대적으로 쉬움
- (서버 측 이벤트가 발생하지 않아도) 주기적으로 통신하는 비용이 발생
- 요청 주기에 따른 실시간성
    - 요청 주기가 짧다면 실시간성은 높아지나 부하가 증가
    - 요청 주기가 길다면 실시간성은 낮아지나 부하는 감소
- 높은 실시간성을 요구하지 않는 서비스에 사용을 고려해볼 수 있음
    - 예) 대시 보드, 날씨 정보 등
    - 네이버 스포츠 문자 중계는 10초 주기의 Polling 방식을 사용 중

### 1.3. 구현 방법

- 클라이언트에서 자바스크립트를 이용해 주기적으로 요청을 보내도록 구현
    - [setTimeout](https://github.com/mynuni/real-time-practice/blob/main/src/main/resources/static/js/polling/polling.js),
      setInterval 등

```javascript
// setTimeout 사용 예시
const poll = () => {
    fetch("/some-end-point")
        .then(response => response.json()) // if needed
        .then(data => {
            // do something
            setTimeout(poll, 1000); // fetch after 1 second
        });
};

// setInterval 사용 예시
const poll = () => {
    setInterval(() => {
        fetch("/some-end-point")
            .then(response => response.json()) // if needed
            .then(data => {
                // do something
            });
    }, 1000); // fetch every 1 second
};
```

## 2. Long Polling

### 2.1. 정의
- 클라이언트가 서버에 요청을 보내면 서버는 응답을 보류하고 나중에 응답
  - 타임아웃, 이벤트 발생 등의 조건에 따라 응답을 보냄
- 클라이언트는 서버로부터 응답을 받으면 다시 요청

### 2.2. 특징
- 짧은 주기로 요청을 계속 보낼 시 발생하는 단점을 보완
- 응답을 지연시키므로 요청 스레드를 물고 있는 시간이 길어질 수 있음
  - 비동기 처리하여 요청 스레드를 풀어주고 비동기 작업 스레드로 오프로드하여 개선 가능

### 2.3. 구현 방법
- 다른 작업 스레드로 오프로드하지 않은 경우
```java
@GetMapping("some-end-point")
public DeferredResult<String> longPolling() {
  DeferredResult<String> deferredResult = new DeferredResult<>();
  try {
    // 5초 후 이벤트가 발생한다고 가정
    Thread.sleep(5_000);
    deferredResult.setResult("Result");
  } catch (InterruptedException e) {
    // ...
  }
  return deferredResult;
}
```
- 다른 작업 스레드로 오프로드하는 경우
```java
private final ExecutorService executorService = Executors.newFixedThreadPool(10); // can be configured

@GetMapping("some-end-point")
public DeferredResult<String> longPolling() {
  DeferredResult<String> deferredResult = new DeferredResult<>();

  // 다른 작업 스레드로 오프로드
  executorService.execute(() -> {
    try {
      // 5초 후 이벤트가 발생한다고 가정
      Thread.sleep(5_000);
      deferredResult.setResult("Result");
    } catch (InterruptedException e) {
      // ...
    }
  });

  // 요청 스레드 풀림
  return deferredResult;
}

```
- subscribe 후 이벤트 발생 시 DeferredResult에 결과를 설정하도록 구현
  - [LongPollingController](https://github.com/mynuni/real-time-practice/blob/main/src/main/java/com/practice/springrealtime/controller/LongPollingController.java)
  - [LongPollingService](https://github.com/mynuni/real-time-practice/blob/main/src/main/java/com/practice/springrealtime/service/LongPollingService.java)

## 3. Server-Sent Events

### 3.1. 정의
- HTTP 프로토콜을 이용하여 서버에서 클라이언트로 메시지를 단방향으로 전송하는 기술

### 3.2. 특징
- 요청-응답 방식이 아닌 최초 커넥션 이후 서버에서 클라이언트로 메시지를 전송
- 클라이언트 to 서버로의 메시지 전송은 필요하지 않고, 서버 to 클라이언트로의 메시지 전송만 필요한 상황에 적합
- 마지막 수신 이벤트 ID(Last-Event-ID) 헤더를 이용하여 중복 이벤트 방지, 유실 이벤트 처리 등에 활용 가능
- 제약 사항
  - 최대 연결 수 제한: [Chrome](https://issues.chromium.org/issues/40329530) 기준 6개, HTTP/2 이후: 100개(기본값)
  - 브라우저 호환성 확인 [Can I use](https://caniuse.com/?search=Server-Sent%20Events)

### 3.3. 구현
<details>
  <summary>메시지 형식</summary>

```http
GET /some-connection-end-point HTTP/1.1
Host: some-host
Accept: text/event-stream
```

```http
HTTP/1.1 200 OK
Content-Type: text/event-stream
Transfer-Encoding: chunked
Cache-Control: no-cache
Connection: keep-alive
```

```http
id: some-id
event: some-event
data: some-data
```
</details>

- [EventSource](https://github.com/mynuni/real-time-practice/blob/main/src/main/resources/static/js/sse/sse.js) 인터페이스 사용
```javascript
const eventSource = new EventSource("/some-connection-end-point");

// onopen, onmessage, onerror 등의 이벤트 핸들러
eventSource.onmessage = (event) => {
    // do something
};
```
- Spring [SseEmitter](https://github.com/mynuni/real-time-practice/blob/main/src/main/java/com/practice/springrealtime/controller/SseController.java) 사용
```java
@GetMapping(value = "/some-connection-end-point", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
public SseEmitter connect(@RequestHeader(value = "Last-Event-ID", required = false) Integer lastEventId) {
  // ...
  return sseEmitter;
}
```
### 3.4. 참고 사항
- 최초 연결 시 503 Service Unavailable 발생 가능
  - 연결 후 타임아웃 전까지 메시지가 없으면 무응답으로 간주하여 연결이 끊어짐
    - 연결 성공 시 이벤트를 바로 전송해주는 것이 좋음
- EventSource는 헤더를 설정할 수 없음
  - 직접 XHR 객체의 헤더를 설정한 후 EventSource를 생성하도록 해야 함
  - fetch, axios 등을 이용하면 약간 편리해지긴 하나, 라이브러리 사용이 더 편리
  - event-source-polyfill, fetch-event-source 등을 이용하면 간단하게 설정 가능
- EventSource의 `onmessage`는 유형이 `"message"`인 이벤트를 처리
  - 유형이 다를 경우 이벤트 리스너를 사용하여 처리
  ```javascript
    eventSource.addEventListener("유형", (event) => { 
        // do something
    });
    ```
- 리버스 프록시 사용 시(Nginx 기준)
  - HTTP 버전 및 Connection 헤더 확인
    - upstream 서버로 요청 시 `HTTP/1.0`인 경우 `Connection: close` 헤더를 사용하므로 지속 연결이 끊어질 수 있음
    - `proxy_set_header Connection ''` 헤더 값 비우기
    - `proxy_http_version 1.1` 버전 명시
  - 버퍼링 사용 안 함
    - 버퍼가 찰 때까지 모았다가 응답을 보내는 경우가 있으므로 실시간성이 떨어질 수 있음
    - `X-Accel-Buffering: no` SSE 연결에 대한 버퍼링을 사용하지 않도록 헤더 설정
    - `proxy_buffering off` 설정
    - 버퍼링 미사용에 따른 부작용이 있을 수 있음
      
## 4. WebSocket

### 4.1. 정의
- 클라이언트와 서버 간 양방향 통신을 가능하게 하는 프로토콜

### 4.2. 특징
- 하나의 TCP 커넥션을 통해 전이중 통신을 지원
- Polling 처럼 주기적으로 요청을 보내지 않아도 됨
- 서버로부터 받기만 하는 SSE와 달리 양방향 통신이 가능
- Connection: upgrade 헤더를 이용하여 HTTP 프로토콜을 WebSocket 프로토콜로 전환
- 전환 요청이 성공하면 서버에서는 101 Switching Protocols 응답
- HTTP/HTTPS 처럼 보안 통신 시 wss 사용

### 4.3. 구현 방법
- WebSocket 객체 이용
  - 커넥션, 데이터 송수신 및 관리에 대한 API 제공
```javascript
const webSocket = new WebSocket("ws://connection-url");

// onopen, onmessage, onerror, onclose 등의 이벤트 핸들러
webSocket.onmessage = (event) => {
    // do something
};
```
- WebSocketHandler 이용
```java
@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {
    
    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(new WebSocketHandler(), "/some-end-point")
                .setAllowedOrigins("Origin URL");
    }
    
}
```
```java
public class WebSocketHandler extends TextWebSocketHandler {
    
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        // 연결 수립 후 처리
    }
    
    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        // 메시지 처리
    }
    
}
```

### 4.4. 참고 사항
- 리버스 프록시 사용 시 추가 구성 필요
    ```nginx
    location /some-end-point {
        proxy_pass upstream-server;
        proxy_http_version 1.1;
        proxy_set_header Upgrade $http_upgrade;
        proxy_set_header Connection "upgrade";
    }
    ```
    proxy_http_version 1.1;
    proxy_set_header Upgrade $http_upgrade;
    proxy_set_header Connection "upgrade";

## 5. STOMP on WebSocket

## 참고
- Polling
  - Baeldung - Long Polling(https://www.baeldung.com/spring-mvc-long-polling)
  - KSUG - Async & Spring(https://www.youtube.com/watch?v=HKlUvCv9hvA) 비동기 처리
- Server-Sent Events
  - MDN Web Docs - Server-Sent Events(https://developer.mozilla.org/en-US/docs/Web/API/Server-sent_events)
  - MDN Web Docs - EventSource(https://developer.mozilla.org/en-US/docs/Web/API/EventSource) EventSource API
  - Nginx - Proxy module(https://nginx.org/en/docs/http/ngx_http_proxy_module.html) upstream HTTP/1.0 issue
- WebSockets
  - RFC 6455 - The WebSocket Protocol(https://datatracker.ietf.org/doc/html/rfc6455)
  - MDN Web Docs - WebSockets(https://developer.mozilla.org/en-US/docs/Web/API/WebSockets_API) WebSockets API
  - Nginx - WebSocket Proxying(https://nginx.org/en/docs/http/websocket.html) WebSocket proxying
  - Spring - WebSockets(https://docs.spring.io/spring-framework/docs/current/reference/html/web.html#websocket)