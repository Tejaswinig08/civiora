package com.civiora.civiora.controller;

import com.civiora.civiora.models.ChatMessage;
import com.civiora.civiora.models.User;
import com.civiora.civiora.repositories.ChatRepo;
import com.civiora.civiora.repositories.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@CrossOrigin(origins = "*")
public class ChatController {

    @Autowired ChatRepo chatRepo;
    @Autowired UserRepo userRepo;

    // ────────────────────────────────────────────────────────────────
    // GET /chat/users
    // Returns list of all users (name, id, role) for the recipient
    // dropdown. The current user is excluded automatically on the frontend.
    // ────────────────────────────────────────────────────────────────
    @GetMapping("/chat/users")
    public List<Map<String, Object>> getAllUsers() {
        List<User> users = userRepo.findAll();
        return users.stream().map(u -> {
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("id",   u.getId());
            m.put("name", u.getName());
            m.put("role", u.getRole());   // "user" or "admin"
            return m;
        }).collect(Collectors.toList());
    }

    // ────────────────────────────────────────────────────────────────
    // GET /chat/messages?mode=public   → all public messages
    // GET /chat/messages?mode=dm&with=<otherId>  → DM thread
    // ────────────────────────────────────────────────────────────────
    @GetMapping("/chat/messages")
    public List<ChatMessage> getMessages(
            @RequestParam(defaultValue = "unified") String mode,
            @RequestParam(defaultValue = "0")        int    userId,
            @RequestParam(defaultValue = "0")        int    with) {

        if ("unified".equals(mode) && userId > 0) {
            return chatRepo.findVisibleToUser(userId);
        }
        if ("dm".equals(mode) && userId > 0 && with > 0) {
            return chatRepo.findDmThread(userId, with);
        }
        return chatRepo.findByReceiverIdIsNullOrderBySentAtAsc();
    }

    // ────────────────────────────────────────────────────────────────
    // GET /chat/messages/since/{lastId}?mode=public
    // GET /chat/messages/since/{lastId}?mode=dm&userId=&with=
    // ────────────────────────────────────────────────────────────────
    @GetMapping("/chat/messages/since/{lastId}")
    public List<ChatMessage> getMessagesSince(
            @PathVariable                            int  lastId,
            @RequestParam(defaultValue = "unified") String mode,
            @RequestParam(defaultValue = "0")        int  userId,
            @RequestParam(defaultValue = "0")        int  with) {

        if ("unified".equals(mode) && userId > 0) {
            return chatRepo.findVisibleToUserSince(userId, lastId);
        }
        if ("dm".equals(mode) && userId > 0 && with > 0) {
            return chatRepo.findDmThreadSince(userId, with, lastId);
        }
        return chatRepo.findPublicSince(lastId);
    }

    // ────────────────────────────────────────────────────────────────
    // POST /chat/send
    // Body: { userId, message, receiverId? }
    //   receiverId omitted / null / 0  → public broadcast
    //   receiverId = <id>              → private DM
    // ────────────────────────────────────────────────────────────────
    @PostMapping("/chat/send")
    public Map<String, Object> sendMessage(@RequestBody Map<String, Object> data) {
        Map<String, Object> response = new HashMap<>();

        int    userId  = Integer.parseInt(data.get("userId").toString());
        String message = data.get("message").toString().trim();

        if (message.isEmpty() || message.length() > 500) {
            response.put("status",  "error");
            response.put("message", "Message must be between 1 and 500 characters.");
            return response;
        }

        Optional<User> optionalUser = userRepo.findById(userId);
        if (!optionalUser.isPresent()) {
            response.put("status",  "error");
            response.put("message", "User not found.");
            return response;
        }

        // Parse optional receiverId (null or 0 → public)
        Integer receiverId = null;
        if (data.containsKey("receiverId") && data.get("receiverId") != null) {
            int rid = Integer.parseInt(data.get("receiverId").toString());
            if (rid > 0) receiverId = rid;
        }

        User user = optionalUser.get();

        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setUserId(userId);
        chatMessage.setSenderName(user.getName());
        chatMessage.setRole(user.getRole().toUpperCase());
        chatMessage.setMessage(message);
        chatMessage.setReceiverId(receiverId);   // null = public
        chatRepo.save(chatMessage);

        response.put("status",     "success");
        response.put("id",         chatMessage.getId());
        response.put("senderName", chatMessage.getSenderName());
        response.put("dm",         receiverId != null);
        return response;
    }
}
