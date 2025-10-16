package com.blogapp.blog_application.service;

import com.blogapp.blog_application.entity.User;

import java.util.List;

public interface UserService {
    List<User> getAllUsers();
    User findUserById(int id);
    void save(User user);
    User findUserByUsername(String email);
}
