package com.blogapp.blog_application.service;

import com.blogapp.blog_application.entity.Post;
import com.blogapp.blog_application.entity.Tag;
import com.blogapp.blog_application.repository.PostRepository;
import com.blogapp.blog_application.repository.TagRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class PostServiceImpl implements PostService{

    private final PostRepository postRepository;
    private final TagRepository tagRepository;

    public PostServiceImpl(PostRepository postRepository, TagRepository tagRepository) {
        this.postRepository = postRepository;
        this.tagRepository = tagRepository;
    }

    public Page<Post> searchFilterAndSortPosts(String keyword, String sortField, String order, List<Integer> authorIds, List<Integer> tagIds, int page, int size) {
        Sort.Direction direction = "desc".equalsIgnoreCase(order) ? Sort.Direction.DESC : Sort.Direction.ASC;
        Sort sort = Sort.by(direction, sortField != null ? sortField : "publishedAt");
        Pageable pageable = PageRequest.of(page, size, sort);

        boolean hasKeyword = keyword != null && !keyword.trim().isEmpty();
        boolean hasFilters = (authorIds != null && !authorIds.isEmpty()) || (tagIds != null && !tagIds.isEmpty());

        if (hasKeyword && hasFilters) {
            return postRepository.searchFilterAndSort(keyword, authorIds, tagIds, pageable);
        } else if (hasKeyword) {
            return postRepository.searchAndSort(keyword, pageable);
        } else if (hasFilters) {
            return postRepository.filterAndSort(authorIds, tagIds, pageable);
        } else {
            return postRepository.findAll(pageable);
        }
    }

    @Override
    @Transactional
    public Post savePost(Post post, String tagString) {
        List<Tag> tags = convertTagStringToTags(tagString);
        post.setTags(tags);

        if (post.getId()!=0) {
            Post existingPost=postRepository.findById(post.getId()).orElse(null);
            if (existingPost!=null) {
                post.setComments(existingPost.getComments());
                post.setCreatedAt(existingPost.getCreatedAt());
            }
        }

        Post savedPost = postRepository.save(post);
        return savedPost;
    }

    private List<Tag> convertTagStringToTags(String tagString) {
        List<Tag> tags = new ArrayList<>();

        if (tagString != null && !tagString.trim().isEmpty()) {
            String[] tagNames = tagString.split(",");

            for (String name : tagNames) {
                String tagName = name.trim();
                if (!tagName.isEmpty()) {
                    Tag tag = getOrCreateTag(tagName);
                    tags.add(tag);
                }
            }
        }

        return tags;
    }

    private Tag getOrCreateTag(String tagName) {
        Tag existingTag = tagRepository.findByName(tagName);
        if (existingTag!=null) {
            return existingTag;
        } else {
            Tag newTag = new Tag(tagName);
            return tagRepository.save(newTag);
        }
    }

    @Override
    public Post getPostById(int id) {
        return postRepository.findById(id).get();
    }

    @Override
    public String getTagStringOfPost(Post post) {
        StringBuilder tags=new StringBuilder();
        List<Tag> tagList=post.getTags();
        for(Tag tag:tagList){
            String tagName=tag.getName();
            tags.append(tagName);
            tags.append(",");
        }
        return tags.toString();
    }

    @Override
    public void deletePostById(int id) {
        postRepository.deleteById(id);
    }
}