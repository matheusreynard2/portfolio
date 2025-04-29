package com.apiestudar.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.apiestudar.entity.ChatMessage;
import com.apiestudar.exceptions.ParametroInformadoNullException;
import com.apiestudar.repository.ChatMessageRepository;

@Service
public class ChatMessageServiceImpl implements ChatMessageService {

	@Autowired
	private ChatMessageRepository chatMessageRepository;

	public List<ChatMessage> findTop50MessagesOrderByTimeStampDesc() {
		return chatMessageRepository.findTop50ByOrderByTimeStampDesc();
	}

	public ChatMessage salvarMensagem(ChatMessage msg) {
	    return Optional.ofNullable(msg)
	            .map(message -> chatMessageRepository.save(message))
	            .orElseThrow(() -> new ParametroInformadoNullException());
	}

}