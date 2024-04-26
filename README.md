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

## 3. Server-Sent Events

## 4. WebSocket

## 5. STOMP on WebSocket