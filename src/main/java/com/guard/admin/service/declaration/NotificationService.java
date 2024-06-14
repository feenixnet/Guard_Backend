package com.guard.admin.service.declaration;


public interface NotificationService {

    boolean sendMessage(String recipientToken, String title, String body);

    void saveToken(String tokenValue, Integer userId, String role);
}
