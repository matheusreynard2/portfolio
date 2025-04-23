import React, { useState, useEffect, useRef } from "react";
// @ts-ignore
import {useWebSocket} from '../contexts/WebSocketContext.tsx';

function Chat1() {
    const [inputValue, setInputValue] = useState("");
    const messagesEndRef = useRef(null)
    const {
        connect,
        subscribe,
        sendMessage,
        setMessages,
        messages,
        checkConnection,
    } = useWebSocket();

    useEffect(() => {
        fetch("http://localhost:8080/api/chat/mensagens")
            .then((response) => response.json())
            .then((data) => {
                // Inverte a ordem se necessário
                const ordered = data.reverse().map((m) => m.message);
                setMessages(ordered);
            });
    }, []);

    connect("http://localhost:8080/chat")
    subscribe()

    useEffect(() => {
        if (messagesEndRef.current) {
            messagesEndRef.current.scrollIntoView({ behavior: "smooth" });
        }
    }, [messages]);

    const handleSend = () => {
        sendMessage(inputValue);
        setInputValue("");
    };

    return (
        <div>
            <h2>Chat 2 {checkConnection ? "true" : "false"}</h2>
            <div>
                {messages.map((msg, idx) => (
                    <p key={idx}>{msg}</p>
                ))}
            </div>
            <input
                type="text"
                value={inputValue}
                onChange={(e) => setInputValue(e.target.value)}
            />
            <button onClick={handleSend}>Enviar</button>
        </div>
    );
}

export default Chat1;