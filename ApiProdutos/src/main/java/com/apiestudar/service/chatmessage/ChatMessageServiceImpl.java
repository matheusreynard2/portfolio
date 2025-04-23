package com.apiestudar.service.chatmessage;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.apiestudar.model.ChatMessage;
import com.apiestudar.repository.ChatMessageRepository;

@Service
public class ChatMessageServiceImpl implements ChatMessageService {
	
	@Autowired
	private ChatMessageRepository chatMessageRepository;
	
	public List<ChatMessage> findTop50MessagesOrderByTimeStampDesc() {
		return chatMessageRepository.findTop50ByOrderByTimeStampDesc();
	}
	
	public ChatMessage salvarMensagem(ChatMessage msg) {
		return chatMessageRepository.save(msg);
	}
	
}