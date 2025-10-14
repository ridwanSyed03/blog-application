package com.blogapp.blog_application.service;

import com.blogapp.blog_application.entity.Comment;
import com.blogapp.blog_application.repository.CommentRepository;
import org.springframework.stereotype.Service;

@Service
public class CommentServiceImpl implements CommentService{
    private final CommentRepository commentRepository;

    public CommentServiceImpl(CommentRepository commentRepository) {
        this.commentRepository = commentRepository;
    }

    @Override
    public Comment getCommentById(int id) {
        return commentRepository.findById(id).orElse(null);
    }

    @Override
    public void saveComment(Comment comment) {
        commentRepository.save(comment);
    }

    @Override
    public void deleteCommentById(int id) {
        commentRepository.deleteById(id);
    }
}
