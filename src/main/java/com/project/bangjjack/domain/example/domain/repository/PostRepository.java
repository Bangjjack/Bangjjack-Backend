package com.project.bangjjack.domain.example.domain.repository;

import com.project.bangjjack.domain.example.domain.entity.Post;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Long> {

	List<Post> findAllByDeletedFalse();

	Optional<Post> findByIdAndDeletedFalse(Long id);
}
