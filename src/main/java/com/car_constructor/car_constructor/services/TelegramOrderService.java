package com.car_constructor.car_constructor.services;

import com.car_constructor.car_constructor.repositories.ChatRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class TelegramOrderService {

    @Value("${telegram.order.bot.token}")
    private String orderBotToken;

    @Value("${telegram.chat.id}")
    private String chatID;




    private final RestTemplate restTemplate = new RestTemplate();

    public void sendOrderNotification(String message){
        String apiUrl = "https://api.telegram.org/bot" + orderBotToken + "/sendMessage?chat_id=" + chatID + "&text=" + message;
        restTemplate.getForObject(apiUrl, String.class);
    }

}
