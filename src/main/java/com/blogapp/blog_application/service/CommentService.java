package com.blogapp.blog_application.service;

import com.blogapp.blog_application.entity.Comment;

public interface CommentService {
    Comment getCommentById(int id);
    void saveComment(Comment comment);
    void deleteCommentById(int id);
}
