package com.civiora.civiora.controller;

import com.civiora.civiora.models.User;
import com.civiora.civiora.repositories.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "*")
public class UserController {

    @Autowired
    UserRepo userRepo;

    @PostMapping("/register")
    public String register(@RequestBody User user) {
        userRepo.save(user);
        return "Signup Successful";
    }

    @PostMapping("/update")
    public String update(@RequestBody com.civiora.civiora.dto.UpdateDto obj) {
        User user = userRepo.findById(obj.getId()).orElseThrow(() -> new RuntimeException("Not Found"));
        if (obj.getKey().equalsIgnoreCase("name")) {
            if (obj.getValue().equals(user.getName())) return "Cannot be same";
            user.setName(obj.getValue());
        } else if (obj.getKey().equalsIgnoreCase("password")) {
            if (obj.getValue().equals(user.getPassword())) return "Cannot be same";
            user.setPassword(obj.getValue());
        } else if (obj.getKey().equalsIgnoreCase("email")) {
            if (obj.getValue().equals(user.getEmail())) return "Cannot be same";
            User user2 = userRepo.findByEmail(obj.getValue());
            if (user2 != null) return "Email already Exists";
            user.setEmail(obj.getValue());
        } else {
            return "Invalid key";
        }
        userRepo.save(user);
        return "Updated Successfully";

    }
    @GetMapping("/user/{id}")
    public User getUser(@PathVariable int id) {
        return userRepo.findById(id).orElse(null);
    }
}
