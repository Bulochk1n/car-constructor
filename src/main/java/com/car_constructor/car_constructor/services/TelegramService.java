package com.car_constructor.car_constructor.services;

import com.car_constructor.car_constructor.models.ChatAdminMessage;
import com.car_constructor.car_constructor.repositories.ChatRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class TelegramService {

    @Value("${telegram.bot.token}")
    private String orderBotToken;
    @Value("7030908519:AAEoLR_8euQ794Cayoew9aj8uhNE1yCdbRs")
    private String chatBotToken;
    @Value("${telegram.chat.id}")
    private String chatID;




    private final RestTemplate restTemplate = new RestTemplate();

    public void sendOrderNotification(String message){
        String apiUrl = "https://api.telegram.org/bot" + orderBotToken + "/sendMessage?chat_id=" + chatID + "&text=" + message;
        restTemplate.getForObject(apiUrl, String.class);
    }

    public void sendChatNotification(String message){
        String apiUrl = "https://api.telegram.org/bot" + chatBotToken + "/sendMessage?chat_id=" + chatID + "&text=" + message;
        restTemplate.getForObject(apiUrl, String.class);
    }


    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private ChatRepository chatRepository;

    public void handleTelegramMessage(String chatId, String content) {
        // Создать новое сообщение и сохранить его
        ChatAdminMessage newMessage = new ChatAdminMessage();
        newMessage.setSender("Admin");
        newMessage.setContent(content);
        chatRepository.save(newMessage);

        // Отправить сообщение на фронт-енд через WebSocket
        messagingTemplate.convertAndSend("/topic/admin-replies", newMessage);
    }
}
