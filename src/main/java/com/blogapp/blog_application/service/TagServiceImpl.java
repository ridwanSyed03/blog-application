package com.blogapp.blog_application.service;

import com.blogapp.blog_application.entity.Tag;
import com.blogapp.blog_application.repository.TagRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TagServiceImpl implements TagService{
    private final TagRepository tagRepository;

    public TagServiceImpl(TagRepository tagRepository) {
        this.tagRepository = tagRepository;
    }

    @Override
    public List<Tag> getTags() {
        return tagRepository.findAll();
    }
}
