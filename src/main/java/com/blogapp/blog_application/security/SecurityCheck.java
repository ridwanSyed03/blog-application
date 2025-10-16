package com.blogapp.blog_application.security;

import com.blogapp.blog_application.entity.Comment;
import com.blogapp.blog_application.entity.Post;
import com.blogapp.blog_application.service.CommentService;
import com.blogapp.blog_application.service.PostService;
import org.springframework.stereotype.Component;

@Component
public class SecurityCheck {
    private final PostService postService;
    private final CommentService commentService;

    public SecurityCheck(PostService postService, CommentService commentService) {
        this.postService = postService;
        this.commentService = commentService;
    }

    public boolean isValidAuthor(String loggedInUsername,int postId){
        Post post=postService.getPostById(postId);
        if(post!=null){
            return post.getUser().getEmail().equalsIgnoreCase(loggedInUsername);
        }
        return false;
    }

    public boolean isValidAuthorForComment(String username, int commentId) {
        Comment comment = commentService.getCommentById(commentId);
        if (comment == null) return false;
        boolean isPostOwner=comment.getPost().getUser().getEmail().equals(username);
        boolean isCommentAuthor=comment.getEmail().equalsIgnoreCase(username);

        return isPostOwner||isCommentAuthor;
    }
}
