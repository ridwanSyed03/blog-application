package com.blogapp.blog_application.controller;

import com.blogapp.blog_application.entity.Comment;
import com.blogapp.blog_application.entity.Post;
import com.blogapp.blog_application.entity.User;
import com.blogapp.blog_application.service.CommentService;
import com.blogapp.blog_application.service.PostService;
import com.blogapp.blog_application.service.UserService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.security.Principal;

@Controller
public class CommentController {
    private final PostService postService;
    private final CommentService commentService;
    private final UserService userService;

    public CommentController(PostService postService, CommentService commentService, UserService userService) {
        this.postService = postService;
        this.commentService = commentService;
        this.userService = userService;
    }

    @PostMapping("/post/{id}/comment")
    public String saveCommentToPost(@PathVariable("id") int id, @ModelAttribute("commentObj") Comment comment, Principal principal){
        Post post=postService.getPostById(id);
        comment.setId(0);
        comment.setPost(post);

        if (principal != null) {
            User loggedInUser = userService.findUserByUsername(principal.getName());
            comment.setName(loggedInUser.getName());
            comment.setEmail(loggedInUser.getEmail());
        }

        commentService.saveComment(comment);

        return "redirect:/post/"+id;
    }

    @PreAuthorize("hasRole('ROLE_AUTHOR') and @securityCheck.isValidAuthorForComment(authentication.name, #id)")
    @GetMapping("/comment/edit/{id}")
    public String editCommentById(@PathVariable("id") int id, Model model){
        Comment comment= commentService.getCommentById(id);
        if(comment!=null){
            model.addAttribute("commentObj",comment);
        }
        return "comment";
    }

    @PostMapping("/comment/save")
    public String saveComment(@ModelAttribute("commentObj") Comment comment, Principal principal){
        int postId=comment.getPost().getId();

        if (principal != null) {
            User loggedInUser = userService.findUserByUsername(principal.getName());
            comment.setEmail(loggedInUser.getEmail());
            comment.setName(loggedInUser.getName());
        }
        commentService.saveComment(comment);
        return "redirect:/post/"+postId;
    }

    @PreAuthorize("hasRole('ROLE_AUTHOR') and @securityCheck.isValidAuthorForComment(authentication.name, #id)")
    @GetMapping("/comment/delete/{id}")
    public String deleteCommentById(@PathVariable int id){
        int postId=commentService.getCommentById(id).getPost().getId();
        commentService.deleteCommentById(id);
        return "redirect:/post/"+postId;
    }
}