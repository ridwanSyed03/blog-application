package com.blogapp.blog_application.repository;

import com.blogapp.blog_application.entity.Tag;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TagRepository extends JpaRepository<Tag,Integer> {
    Tag findByName(String name);
}
