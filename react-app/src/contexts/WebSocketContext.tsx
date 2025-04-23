import React, {createContext, ReactNode, useContext, useEffect, useState} from "react";
import { Client } from "@stomp/stompjs";
import * as SockJS from "sockjs-client";

// Tipos do contexto
interface WebSocketContextType {
    isConnected: boolean;
    messages: string[];
    sendMessage: (message: string) => void;
    connect: (websocketURL: string) => void;
    subscribe: () => void;
    setMessages: (message: any) => void;
    checkConnection: () => boolean;
}

const WebSocketContext = createContext<WebSocketContextType | undefined>(undefined);

// Props para o Provider
interface WebSocketProviderProps {
    children: ReactNode;
}

export const WebSocketProvider = ({ children }: WebSocketProviderProps) => {
    const [isConnected, setIsConnected] = useState(false);
    const [messages, setMessages] = useState([]);

    let conexao: Client = null;

    const connect = (websocketURL) =>{
        const socket = new SockJS("http://localhost:8080/chat");
        const client = new Client({
            webSocketFactory: () => socket,
            onConnect: () => {
                console.log("Conectado ao servidor WebSocket (Chat 2)");
                setIsConnected(true);
            },
            onStompError: (frame) => {
                console.error("Erro no STOMP:", frame);
            },
        });

        client.activate();
        conexao = client;

        return () => {
            conexao.deactivate();
        };
    }

    const subscribe = () => {
        console.log(conexao)
        conexao.subscribe("/topic/chat", (message) => {
            if (message.body) {
                setMessages((prev) => [...prev, message.body]);
            }
        });
    }

    const sendMessage = (inputValue) => {
        if (conexao && isConnected && inputValue.trim() !== "") {
            conexao.publish({
                destination: "/app/chat",
                body: inputValue,
            });
        } else {
            console.warn("A conexão STOMP ainda não está pronta.");
        }
    };

    // Verifica o status da conexão
    const checkConnection = (): boolean => {
        return isConnected;
    };

    return (
        <WebSocketContext.Provider
            value={{
                isConnected,
                messages,
                sendMessage,
                connect,
                subscribe,
                setMessages,
                checkConnection
            }}
        >
            {children}
        </WebSocketContext.Provider>
    );
};

export const useWebSocket = (): WebSocketContextType => {
    const context = useContext(WebSocketContext);
    if (!context) {
        throw new Error("useWebSocket deve ser usado dentro de um WebSocketProvider");
    }
    return context;
};