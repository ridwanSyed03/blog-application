package com.blogapp.blog_application.api;

import com.blogapp.blog_application.entity.Comment;
import com.blogapp.blog_application.entity.Post;
import com.blogapp.blog_application.entity.Tag;
import com.blogapp.blog_application.entity.User;
import com.blogapp.blog_application.service.PostService;
import com.blogapp.blog_application.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/blog")
public class PostApiController {
    private final PostService postService;
    private final UserService userService;

    public PostApiController(PostService postService, UserService userService) {
        this.postService = postService;
        this.userService = userService;
    }

    @GetMapping("/posts")
    public List<Post> getPosts(
            @RequestParam(value = "search", required = false) String keyword,
            @RequestParam(value = "sortField", required = false) String sortField,
            @RequestParam(value = "order", required = false) String order,
            @RequestParam(value = "authorId", required = false) List<Integer> authorIds,
            @RequestParam(value = "tagId", required = false) List<Integer> tagIds,
            @RequestParam(defaultValue = "0") int page) {

        int pageSize = 10;

        Page<Post> postPage = postService.searchFilterAndSortPosts(keyword, sortField, order, authorIds, tagIds, page, pageSize);

        return postPage.getContent();
    }

    @PostMapping("/posts")
    public Post createOrUpdatePost(@RequestBody Post post, @RequestParam String tagString, Principal principal){
        String username=principal.getName();

        if(username!=null){
            User user=userService.findUserByUsername(username);
            post.setUser(user);
        }
        post.setPublished(true);

        String content = post.getContent();
        int excerptLength = Math.min(content.length(), 200);
        post.setExcerpt(content.substring(0, excerptLength));

        Post postSaved=postService.savePost(post,tagString);

        return postSaved;
    }

    @GetMapping("/posts/{id}")
    public Post getPost(@PathVariable int id){
        return postService.getPostById(id);
    }

    @GetMapping("/posts/{id}/comments")
    public List<Comment> getCommentsByPostId(@PathVariable int id) {
        Post post=postService.getPostById(id);
        return post.getComments();
    }

    @PreAuthorize("hasRole('ROLE_ADMIN') or (hasRole('ROLE_AUTHOR') and @securityCheck.isValidAuthor(authentication.name, #id))")
    @PutMapping("/posts/{id}")
    public Post updatePost(@PathVariable int id,@RequestBody Post updatedPost,@RequestParam String tagString) {
        Post existingPost = postService.getPostById(id);

        existingPost.setTitle(updatedPost.getTitle());
        existingPost.setContent(updatedPost.getContent());
        existingPost.setExcerpt(updatedPost.getContent().substring(0, Math.min(updatedPost.getContent().length(), 200)));

        return postService.savePost(existingPost, tagString);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN') or (hasRole('ROLE_AUTHOR') and @securityCheck.isValidAuthor(authentication.name, #id))")
    @PatchMapping("/posts/{id}")
    public Post partialUpdatePost(@PathVariable int id,@RequestBody Post updatedPost,@RequestParam(defaultValue = "") String tagString) {
        Post existingPost = postService.getPostById(id);

        if(updatedPost.getTitle()!=null){
            existingPost.setTitle(updatedPost.getTitle());
        }
        if(updatedPost.getContent()!=null){
            existingPost.setContent(updatedPost.getContent());
            existingPost.setExcerpt(updatedPost.getContent().substring(0, Math.min(updatedPost.getContent().length(), 200)));
        }
        if("".equals(tagString)){
            tagString=convertTagListToTagString(existingPost.getTags());
        }

        return postService.savePost(existingPost, tagString);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN') or (hasRole('ROLE_AUTHOR') and @securityCheck.isValidAuthor(authentication.name, #id))")
    @DeleteMapping("/posts/{id}")
    public void deletePostById(@PathVariable int id){
        postService.deletePostById(id);
    }

    String convertTagListToTagString(List<Tag> tags){
        StringBuilder tagString=new StringBuilder();
        for(Tag tag:tags){
            tagString.append(tag.getName());
            tagString.append(",");
        }
        return tagString.toString();
    }
}
