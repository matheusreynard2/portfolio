// Declaração para sockjs-client
declare module 'sockjs-client' {
    class SockJS {
        constructor(url: string);
        close(): void;
        onopen: (() => void) | null;
        onmessage: ((event: { data: string }) => void) | null;
        onclose: (() => void) | null;
    }
    export default SockJS;
}

// Declaração para stompjs
declare module 'stompjs' {
    export class Client {
        webSocketFactory: () => WebSocket;
        connect(headers?: any, connectCallback?: () => void): void;
        subscribe(destination: string, callback: (message: { body: string }) => void): void;
        publish(args: { destination: string; body: string }): void;
        deactivate(): void;
    }
}