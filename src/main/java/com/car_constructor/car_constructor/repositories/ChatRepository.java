package com.car_constructor.car_constructor.repositories;

import com.car_constructor.car_constructor.models.ChatAdminMessage;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ChatRepository extends CrudRepository<ChatAdminMessage, Long> {
    List<ChatAdminMessage> findBySenderOrRecipient(String sender, String recipient);
}
