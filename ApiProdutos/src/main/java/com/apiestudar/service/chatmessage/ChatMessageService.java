package com.apiestudar.service.chatmessage;

import java.util.List;

import com.apiestudar.model.ChatMessage;
import com.apiestudar.model.Curso;

public interface ChatMessageService {
	
	List<ChatMessage> findTop50MessagesOrderByTimeStampDesc();
	
	ChatMessage salvarMensagem(ChatMessage msg);
	
}