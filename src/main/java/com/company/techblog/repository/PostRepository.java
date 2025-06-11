package com.company.techblog.repository;

import com.company.techblog.domain.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Long> {

    // JPQL의 DISTINCT**는 중복 엔티티를 제거하여 JPA 내부에서 객체 그래프 중복 생성을 막는 역할
    @Query("SELECT DISTINCT p FROM Post p JOIN FETCH p.author WHERE p.id = :id")
    Optional<Post> findById(Long id);

    @Query("SELECT DISTINCT p FROM Post p JOIN FETCH p.author ORDER BY p.createdAt DESC")
    List<Post> findAll();
}