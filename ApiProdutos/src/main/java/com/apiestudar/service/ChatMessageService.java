package com.apiestudar.service;

import java.util.List;

import com.apiestudar.entity.ChatMessage;
import com.apiestudar.entity.Curso;

public interface ChatMessageService {
	
	List<ChatMessage> findTop50MessagesOrderByTimeStampDesc();
	
	ChatMessage salvarMensagem(ChatMessage msg);
	
}