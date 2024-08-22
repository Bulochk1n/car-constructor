package com.car_constructor.car_constructor.controllers;

import com.car_constructor.car_constructor.models.ChatAdminMessage;
import com.car_constructor.car_constructor.models.ChatMessage;
import com.car_constructor.car_constructor.models.MessageType;
import com.car_constructor.car_constructor.repositories.ChatRepository;
import com.car_constructor.car_constructor.services.MyUserDetailsService;
import com.car_constructor.car_constructor.services.TelegramService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Collections;

@Controller
public class ChatController {

    @Autowired
    private ChatRepository chatRepository;

    @Autowired
    private TelegramService telegramService;


    public ChatController(ChatRepository chatRepository) {
        this.chatRepository = chatRepository;
    }


    @GetMapping("/chat")
    public String openChat(Model model) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUserName = authentication.getName();
        System.out.println(currentUserName);

        if(authentication.isAuthenticated() && !"anonymousUser".equals(currentUserName)) {
            System.out.println(currentUserName);
            model.addAttribute("name", currentUserName);
        }

        return "index";
    }

    @MessageMapping("/chat.sendMessage")
    @SendTo("/topic/public")
    public ChatMessage sendMessage(
            @Payload ChatMessage chatMessage
    ) {
        return chatMessage;
    }

    @MessageMapping("/chat.addUser")
    @SendTo("/topic/public")
    public ChatMessage addUser(
            @Payload ChatMessage chatMessage,
            SimpMessageHeaderAccessor headerAccessor
    ) {
        // Add username in web socket session
        headerAccessor.getSessionAttributes().put("username", chatMessage.getSender());
        return chatMessage;
    }

    @GetMapping("/chat-with-admin/{id}")
    public String openChatWithAdmin(Model model, @PathVariable(value = "id") Long id) {
        model.addAttribute("id", id);
        model.addAttribute("messages", chatRepository.findAllById(Collections.singleton(id)));
        return "chat-with-admin";
    }

    @PostMapping("/chat-with-admin/{id}")
    public String sendMessageToAdmin(Model model, @PathVariable(value = "id") Long id, @RequestParam("content") String content)  {
        ChatAdminMessage newMessage = new ChatAdminMessage();
        newMessage.setType(MessageType.CHAT);
        newMessage.setContent(content);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUserName = authentication.getName();
        newMessage.setSender(currentUserName);

        chatRepository.save(newMessage);

        String message = newMessage.getSender() + ": " + content;
        telegramService.sendChatNotification(message);
        return "chat-with-admin";
    }
}