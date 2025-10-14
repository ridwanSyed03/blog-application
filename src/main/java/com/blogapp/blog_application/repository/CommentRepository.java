package com.blogapp.blog_application.repository;

import com.blogapp.blog_application.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment,Integer> {
}
