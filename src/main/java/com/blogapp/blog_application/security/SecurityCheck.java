package com.blogapp.blog_application.security;

import com.blogapp.blog_application.entity.Post;
import com.blogapp.blog_application.service.PostService;
import org.springframework.stereotype.Component;

@Component
public class SecurityCheck {
    private final PostService postService;

    public SecurityCheck(PostService postService) {
        this.postService = postService;
    }

    public boolean isValidAuthor(String loggedInUsername,int postId){
        Post post=postService.getPostById(postId);
        if(post!=null){
            return post.getUser().getEmail().equalsIgnoreCase(loggedInUsername);
        }
        return false;
    }
}
