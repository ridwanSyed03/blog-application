package com.blogapp.blog_application.controller;

import com.blogapp.blog_application.entity.Comment;
import com.blogapp.blog_application.entity.Post;
import com.blogapp.blog_application.repository.PostRepository;
import com.blogapp.blog_application.service.CommentService;
import com.blogapp.blog_application.service.PostService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class CommentController {
    private final PostRepository postRepository;
    private final PostService postService;
    private final CommentService commentService;

    public CommentController(PostRepository postRepository, PostService postService, CommentService commentService) {
        this.postRepository = postRepository;
        this.postService = postService;
        this.commentService = commentService;
    }

    @PostMapping("/post/{id}/comment")
    public String saveCommentToPost(@PathVariable("id") int id, @ModelAttribute("commentObj") Comment comment){
        Post post=postService.getPostById(id);
        comment.setId(0);
        comment.setPost(post);

        post.getComments().add(comment);
        postRepository.save(post);

        return "redirect:/post/"+id;
    }

    @GetMapping("/comment/edit/{id}")
    public String editCommentById(@PathVariable("id") int id, Model model){
        Comment comment= commentService.getCommentById(id);
        if(comment!=null){
            model.addAttribute("commentObj",comment);
        }
        return "comment";
    }

    @PostMapping("/comment/save")
    public String saveComment(@ModelAttribute("commentObj") Comment comment){
        int postId=comment.getPost().getId();
        commentService.saveComment(comment);
        return "redirect:/post/"+postId;
    }

    @GetMapping("/comment/delete/{id}")
    public String deleteCommentById(@PathVariable int id){
        int postId=commentService.getCommentById(id).getPost().getId();
        commentService.deleteCommentById(id);
        return "redirect:/post/"+postId;
    }
}