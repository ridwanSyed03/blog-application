package com.blogapp.blog_application.repository;

import com.blogapp.blog_application.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface PostRepository extends JpaRepository<Post, Integer> {

    @Query("SELECT DISTINCT p FROM Post p " +
            "LEFT JOIN p.tags t " +
            "LEFT JOIN p.user u " +
            "WHERE (:keyword IS NULL OR :keyword = '' " +
            "   OR LOWER(p.title) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "   OR LOWER(p.content) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "   OR LOWER(t.name) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<Post> searchAndSort(@Param("keyword") String keyword, Pageable pageable);

    @Query("SELECT DISTINCT p FROM Post p " +
            "LEFT JOIN p.tags t " +
            "LEFT JOIN p.user u " +
            "WHERE (:authorIds IS NULL OR u.id IN :authorIds) " +
            "AND (:tagIds IS NULL OR t.id IN :tagIds)")
    Page<Post> filterAndSort(@Param("authorIds") List<Integer> authorIds,
                             @Param("tagIds") List<Integer> tagIds,
                             Pageable pageable);

    @Query("SELECT DISTINCT p FROM Post p " +
            "LEFT JOIN p.tags t " +
            "LEFT JOIN p.user u " +
            "WHERE (:keyword IS NULL OR :keyword = '' " +
            "   OR LOWER(p.title) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "   OR LOWER(p.content) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "   OR LOWER(t.name) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
            "AND (:authorIds IS NULL OR u.id IN :authorIds) " +
            "AND (:tagIds IS NULL OR t.id IN :tagIds)")
    Page<Post> searchFilterAndSort(@Param("keyword") String keyword,
                                   @Param("authorIds") List<Integer> authorIds,
                                   @Param("tagIds") List<Integer> tagIds,
                                   Pageable pageable);
}
