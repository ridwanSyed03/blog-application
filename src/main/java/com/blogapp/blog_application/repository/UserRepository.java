package com.blogapp.blog_application.repository;

import com.blogapp.blog_application.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User,Integer> {
}
