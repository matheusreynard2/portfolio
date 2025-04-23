import React, { useState, useEffect, useRef } from "react";
import { Client } from "@stomp/stompjs";
import * as SockJS from "sockjs-client";

function Chat2() {
    const [messages, setMessages] = useState([]);
    const [inputValue, setInputValue] = useState("");
    const [stompClient, setStompClient] = useState(null);
    const [isConnected, setIsConnected] = useState(false);
    const messagesEndRef = useRef(null);

    useEffect(() => {
        fetch("http://localhost:8080/api/chat/mensagens")
            .then((response) => response.json())
            .then((data) => {
                // Inverte a ordem se necessário
                const ordered = data.reverse().map((m) => m.message);
                setMessages(ordered);
            });
    }, []);

    useEffect(() => {
        if (messagesEndRef.current) {
            messagesEndRef.current.scrollIntoView({ behavior: "smooth" });
        }
    }, [messages]);

    useEffect(() => {
        const socket = new SockJS("http://localhost:8080/chat");
        const client = new Client({
            webSocketFactory: () => socket,
            onConnect: () => {
                console.log("Conectado ao servidor WebSocket (Chat 2)");
                setIsConnected(true);

                // Subscrição no tópico correto
                client.subscribe("/topic/chat", (message) => {
                    if (message.body) {
                        setMessages((prevMessages) => [...prevMessages, message.body]);
                    }
                });
            },
            onStompError: (frame) => {
                console.error("Erro no STOMP:", frame);
            },
        });

        client.activate();
        setStompClient(client);

        return () => {
            client.deactivate();
        };
    }, []);

    const sendMessage = () => {
        if (stompClient && isConnected && inputValue.trim() !== "") {
            stompClient.publish({
                destination: "/app/chat", // Correto conforme backend
                body: inputValue,
            });
            setInputValue("");
        } else {
            console.warn("A conexão STOMP ainda não está pronta.");
        }
    };

    return (
        <div className="container" style={{ marginTop: "80px", marginBottom: "20px" }}>
            <div className="row">
                <div className="col">
                    <h2 className="mb-4" style={{ textAlign: "center" }}>CHAT 2</h2>
                    <div
                        style={{
                            border: "1px solid #ccc",
                            maxHeight: "300px",
                            overflowY: "auto",
                            padding: "10px",
                        }}
                    >
                        {messages.map((msg, index) => (
                            <p key={index}>{msg}</p>
                        ))}
                        <div ref={messagesEndRef}></div>
                    </div>
                    <input
                        type="text"
                        value={inputValue}
                        onChange={(e) => setInputValue(e.target.value)}
                        placeholder="Digite sua mensagem"
                        style={{ width: "80%", padding: "5px", marginTop: "10px" }}
                    />
                    <button
                        onClick={sendMessage}
                        disabled={!isConnected}
                        style={{
                            marginLeft: "10px",
                            padding: "5px 10px",
                            cursor: isConnected ? "pointer" : "not-allowed",
                        }}
                    >
                        Enviar
                    </button>
                </div>
            </div>
        </div>
    );
}

export default Chat2;