package com.civiora.civiora.controller;

import com.civiora.civiora.models.ChatMessage;
import com.civiora.civiora.models.User;
import com.civiora.civiora.repositories.ChatRepo;
import com.civiora.civiora.repositories.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@CrossOrigin(origins = "*")
public class ChatController {

    @Autowired
    ChatRepo chatRepo;

    @Autowired
    UserRepo userRepo;

    // Get ALL messages (initial load)
    @GetMapping("/chat/messages")
    public List<ChatMessage> getAllMessages() {
        return chatRepo.findAllByOrderBySentAtAsc();
    }

    // Poll: get messages newer than lastId
    @GetMapping("/chat/messages/since/{lastId}")
    public List<ChatMessage> getMessagesSince(@PathVariable int lastId) {
        return chatRepo.findByIdGreaterThanOrderBySentAtAsc(lastId);
    }

    // Send a message
    @PostMapping("/chat/send")
    public Map<String, Object> sendMessage(@RequestBody Map<String, Object> data) {
        Map<String, Object> response = new HashMap<>();

        int userId = Integer.parseInt(data.get("userId").toString());
        String message = data.get("message").toString().trim();

        if (message.isEmpty() || message.length() > 500) {
            response.put("status", "error");
            response.put("message", "Message must be between 1 and 500 characters.");
            return response;
        }

        Optional<User> optionalUser = userRepo.findById(userId);
        if (!optionalUser.isPresent()) {
            response.put("status", "error");
            response.put("message", "User not found.");
            return response;
        }

        User user = optionalUser.get();

        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setUserId(userId);
        chatMessage.setSenderName(user.getName());
        chatMessage.setRole(user.getRole().toUpperCase());
        chatMessage.setMessage(message);
        chatRepo.save(chatMessage);

        response.put("status", "success");
        response.put("id", chatMessage.getId());
        response.put("senderName", chatMessage.getSenderName());
        return response;
    }
}
