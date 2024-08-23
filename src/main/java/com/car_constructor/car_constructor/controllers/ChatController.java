package com.car_constructor.car_constructor.controllers;

import com.car_constructor.car_constructor.models.ChatAdminMessage;
import com.car_constructor.car_constructor.models.ChatMessage;
import com.car_constructor.car_constructor.models.MessageType;
import com.car_constructor.car_constructor.repositories.ChatRepository;
import com.car_constructor.car_constructor.services.TelegramChatService;
import com.car_constructor.car_constructor.services.TelegramOrderService;
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

import java.util.List;

@Controller
public class ChatController {

    @Autowired
    private ChatRepository chatRepository;

    @Autowired
    private TelegramChatService telegramChatService;


    public ChatController(ChatRepository chatRepository) {
        this.chatRepository = chatRepository;
    }


    @GetMapping("/chat")
    public String openChat(Model model) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUserName = authentication.getName();

        if(authentication.isAuthenticated() && !"anonymousUser".equals(currentUserName)) {
            model.addAttribute("name", currentUserName);
        }

        return "chat";
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

    @GetMapping("/chat-with-admin/{username}")
    public String openChatWithAdmin(Model model, @PathVariable String username) {
        List<ChatAdminMessage> messages = chatRepository.findBySenderOrRecipient(username, "admin");


        model.addAttribute("messages", messages);
        model.addAttribute("username", username);
        return "chat-with-admin";
    }

    @PostMapping("/chat-with-admin/{username}")
    public String sendMessageToAdmin(@PathVariable String username, @RequestParam("content") String content) {
        ChatAdminMessage newMessage = new ChatAdminMessage();
        newMessage.setSender(username);
        newMessage.setRecipient("admin");
        newMessage.setContent(content);
        newMessage.setType(MessageType.CHAT);

        chatRepository.save(newMessage);

        // Отправляем сообщение в Telegram
        telegramChatService.sendChatNotification(username + " : " + content);

        return "redirect:/chat-with-admin/" + username;
    }
}