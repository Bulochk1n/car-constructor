package com.car_constructor.car_constructor.services;

import com.car_constructor.car_constructor.models.ChatAdminMessage;
import com.car_constructor.car_constructor.repositories.ChatRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.updates.SetWebhook;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.generics.WebhookBot;

@Service
public class TelegramChatService implements WebhookBot {

    @Value("${telegram.chat.bot.token}")
    private String chatBotToken;

    @Value("${telegram.chat.bot.username}")
    private String chatBotUsername;

    @Value("${telegram.chat.id}")
    private String chatID;

    @Value("${telegram.chat.webhook.path}")
    private String chatWebhookPath;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private ChatRepository chatRepository;



    private final RestTemplate restTemplate = new RestTemplate();

    public void sendChatNotification(String message){
        String apiUrl = "https://api.telegram.org/bot" + chatBotToken + "/sendMessage?chat_id=" + chatID + "&text=" + message;
        restTemplate.getForObject(apiUrl, String.class);
    }



    @Override
    public String getBotUsername() {
        return chatBotUsername;
    }


    @Override
    public String getBotToken() {
        return chatBotToken;
    }

    @Override
    public String getBotPath() {
        return chatWebhookPath;
    }

    @Override
    public BotApiMethod<?> onWebhookUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            Message message = update.getMessage();
            String chatId = message.getChatId().toString();
            String receivedText = message.getText();

            System.out.println(chatId);
            System.out.println(receivedText);
            // Логика отправки ответа пользователю
            handleIncomingMessage(chatId, receivedText);
        }
        return null;
    }

    private void handleIncomingMessage(String chatId, String receivedText) {
        ChatAdminMessage newMessage = new ChatAdminMessage();
        newMessage.setSender("admin");
        newMessage.setRecipient(getUsernameFromReceivedText(chatId));
        newMessage.setContent(receivedText);
        System.out.println(receivedText);
        chatRepository.save(newMessage);

        // Отправка сообщения на веб-клиента через WebSocket
        messagingTemplate.convertAndSendToUser(newMessage.getRecipient(), "/queue/reply", newMessage);
    }

    private String getUsernameFromReceivedText(String receivedText) {
        String username = "";
        if (receivedText != null) {
            String[] words = receivedText.split(" ");
            username = words[0];
        }
        return username;
    }

    @Override
    public void setWebhook(SetWebhook setWebhook) {
        // Вы можете оставить этот метод пустым, если вам не нужно явное управление вебхуком
        // Этот метод может быть использован для установки вебхука программно
    }


}




