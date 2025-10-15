package com.blogapp.blog_application.controller;

import com.blogapp.blog_application.entity.Comment;
import com.blogapp.blog_application.entity.Post;
import com.blogapp.blog_application.entity.Tag;
import com.blogapp.blog_application.entity.User;
import com.blogapp.blog_application.service.PostService;
import com.blogapp.blog_application.service.TagService;
import com.blogapp.blog_application.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class PostController {

    private final PostService postService;
    private final TagService tagService;
    private final UserService userService;

    @Autowired
    PostController(PostService postService, TagService tagService, UserService userService){
        this.postService = postService;
        this.tagService = tagService;
        this.userService = userService;
    }

    @GetMapping("/")
    public String getPosts(
            @RequestParam(value = "search", required = false) String keyword,
            @RequestParam(value = "sortField", required = false) String sortField,
            @RequestParam(value = "order", required = false) String order,
            @RequestParam(value = "authorId", required = false) List<Integer> authorIds,
            @RequestParam(value = "tagId", required = false) List<Integer> tagIds,
            @RequestParam(defaultValue = "0") int page,
            Model model) {

        int pageSize = 6;

        Page<Post> postPage = postService.searchFilterAndSortPosts(keyword, sortField, order, authorIds, tagIds, page, pageSize);

        List<Post> posts = postPage.getContent();

        List<Tag> tags = tagService.getTags();
        List<User> authors = userService.getAllUsers();

        model.addAttribute("posts", posts);
        model.addAttribute("keyword", keyword);
        model.addAttribute("tags", tags);
        model.addAttribute("authors", authors);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", postPage.getTotalPages());
        model.addAttribute("order", order);
        model.addAttribute("sortField", sortField);
        model.addAttribute("selectedAuthorIds", authorIds);
        model.addAttribute("selectedTagIds", tagIds);

        return "home";
    }


    @GetMapping("/newpost")
    public String showCreatePostForm(Model model){
        model.addAttribute("post",new Post());
        return "create-post";
    }

    @PostMapping("/newpost")
    public String createOrUpdatePost(@ModelAttribute Post post, @RequestParam String tagString){
        User user=userService.findUserById(1);
        post.setUser(user);
        post.setPublished(true);

        String content = post.getContent();
        int excerptLength = Math.min(content.length(), 200);
        post.setExcerpt(content.substring(0, excerptLength));

        Post postSaved=postService.savePost(post,tagString);

        return "redirect:/post/"+postSaved.getId();
    }

    @GetMapping("/post/{id}")
    public String getPost(@PathVariable int id, Model model){
        Post post=postService.getPostById(id);
        model.addAttribute("post",post);
        model.addAttribute("user",post.getUser());
        model.addAttribute("comments",post.getComments());
        model.addAttribute("commentObj", new Comment());
        return "post";
    }

    @PreAuthorize("hasRole('ROLE_ADMIN') or (hasRole('ROLE_AUTHOR') and @securityCheck.isValidAuthor(authentication.name, #id))")
    @GetMapping("/update/{id}")
    public String updatePost(@PathVariable int id, Model model) {
        Post post=postService.getPostById(id);
        if(post!=null){
            String tags=postService.getTagStringOfPost(post);
            model.addAttribute("post",post);
            model.addAttribute("tagString",tags.toString());
        }

        return "create-post";
    }

    @PreAuthorize("hasRole('ROLE_ADMIN') or (hasRole('ROLE_AUTHOR') and @securityCheck.isValidAuthor(authentication.name, #id))")
    @GetMapping("/delete/{id}")
    public String deletePostById(@PathVariable int id){
        postService.deletePostById(id);
        return "redirect:/";
    }
}