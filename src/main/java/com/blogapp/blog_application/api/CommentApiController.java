package com.blogapp.blog_application.api;

import com.blogapp.blog_application.entity.Comment;
import com.blogapp.blog_application.entity.Post;
import com.blogapp.blog_application.entity.User;
import com.blogapp.blog_application.service.CommentService;
import com.blogapp.blog_application.service.PostService;
import com.blogapp.blog_application.service.UserService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/blog")
public class CommentApiController {
    private final PostService postService;
    private final CommentService commentService;
    private final UserService userService;

    public CommentApiController(PostService postService, CommentService commentService, UserService userService) {
        this.postService = postService;
        this.commentService = commentService;
        this.userService = userService;
    }

    @PostMapping("/posts/{id}/comment")
    public Post saveCommentToPost(@PathVariable("id") int id, @RequestBody Comment comment, Principal principal){
        Post post=postService.getPostById(id);
        comment.setId(0);
        comment.setPost(post);

        if (principal != null) {
            User loggedInUser = userService.findUserByUsername(principal.getName());
            comment.setName(loggedInUser.getName());
            comment.setEmail(loggedInUser.getEmail());
        }
        commentService.saveComment(comment);

        return post;
    }

    @GetMapping("/posts/comment/{id}")
    public Comment commentById(@PathVariable int id){
        return commentService.getCommentById(id);
    }

    @PreAuthorize("hasRole('ROLE_AUTHOR') and @securityCheck.isValidAuthorForComment(authentication.name, #id)")
    @DeleteMapping("/posts/comment/{id}")
    public void deleteCommentById(@PathVariable int id){
        commentService.deleteCommentById(id);
    }

    @PreAuthorize("@securityCheck.isValidAuthorForComment(authentication.name, #id)")
    @PutMapping("/posts/comment/{id}")
    public void updateCommentToPost(@PathVariable("id") int id, @RequestBody Comment comment, Principal principal){
        Comment existingComment=commentService.getCommentById(id);

        if (principal != null) {
            User loggedInUser = userService.findUserByUsername(principal.getName());
            existingComment.setName(loggedInUser.getName());
            existingComment.setEmail(loggedInUser.getEmail());
        }
        existingComment.setComment(comment.getComment());
        commentService.saveComment(existingComment);
    }
}
