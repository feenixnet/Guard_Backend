package com.guard.admin.service.declaration;
import java.util.Map;


public interface NotificationService {

    boolean sendMessage(String recipientToken, String title, String body, Map<String, String> additionalParams);

    boolean sendMessage(String recipientToken, String title, String body);

    void saveToken(String tokenValue, Integer userId, String role);
}
