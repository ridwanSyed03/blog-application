package com.blogapp.blog_application.service;

import com.blogapp.blog_application.entity.Post;
import org.springframework.data.domain.Page;

import java.util.List;

public interface PostService {
    Page<Post> searchFilterAndSortPosts(String keyword, String sortField, String order, List<Integer> authorId, List<Integer> tagId, int page, int size);
    Post savePost(Post post,String tagString);
    Post getPostById(int id);
    String getTagStringOfPost(Post post);
    void deletePostById(int id);
}
