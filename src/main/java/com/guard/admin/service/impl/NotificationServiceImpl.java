package com.guard.admin.service.impl;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Notification;
import com.google.firebase.messaging.Message;
import com.guard.admin.database.entities.Token;
import com.guard.admin.database.repositories.TokenRepository;
import com.guard.admin.service.declaration.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class NotificationServiceImpl implements NotificationService {

    @Autowired
    TokenRepository tokenRepository;

    @Override
    public boolean sendMessage(String recipientToken, String title, String body) {
        try {
            Message message = Message.builder()
                    .setToken(recipientToken)
                    .setNotification(Notification.builder()
                            .setTitle(title)
                            .setBody(body)
                            .build())
                    .build();

            String response = FirebaseMessaging.getInstance().send(message);
            System.out.println("Successfully sent message: " + response);
            return true;
        } catch (FirebaseMessagingException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    @Transactional
    public void saveToken(String tokenValue, Integer userId, String role) {

        tokenRepository.deleteAllByUserIdAndRole(userId, role);

        Token token = new Token();
        token.setToken(tokenValue);
        token.setRole(role);
        token.setUserId(userId);
        tokenRepository.save(token);
    }
}
