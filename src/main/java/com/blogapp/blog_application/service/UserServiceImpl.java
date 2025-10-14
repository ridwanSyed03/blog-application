package com.blogapp.blog_application.service;

import com.blogapp.blog_application.entity.User;
import com.blogapp.blog_application.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService{
    private UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public User findUserById(int id) {
        return userRepository.findById(id).get();
    }
}
