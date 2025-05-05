package com.apiestudar.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.apiestudar.entity.ChatMessage;
import com.apiestudar.service.ChatMessageService;

import springfox.documentation.annotations.ApiIgnore;

@ApiIgnore
@RestController
@RequestMapping("api/chat")
@CrossOrigin(
	    origins = {
	    	"http://localhost:4200",
	        "http://localhost:8080",
	        "https://www.sistemaprodify.com",
	        "https://www.sistemaprodify.com:8080",
	        "https://www.sistemaprodify.com:80",
	        "https://191.252.38.22:8080",
	        "https://191.252.38.22:80",
	        "https://191.252.38.22"
	    },
	    allowedHeaders = {"*"}
)
public class ChatMessageController {

	@Autowired
	private ChatMessageService cursoService;

    @GetMapping("/mensagens")
    public ResponseEntity<List<ChatMessage>> getRecentMessages() {
		List<ChatMessage> msgs = cursoService.findTop50MessagesOrderByTimeStampDesc();
		return ResponseEntity.status(HttpStatus.OK).body(msgs);
    }
}