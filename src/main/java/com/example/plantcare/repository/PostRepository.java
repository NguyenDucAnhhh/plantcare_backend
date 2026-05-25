package com.example.plantcare.repository;
import com.example.plantcare.model.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {
    List<Post> findAllByOrderByCreatedAtDesc();
    List<Post> findByAuthorIdOrderByCreatedAtDesc(Long authorId);
    List<Post> findByAuthorIdInOrderByCreatedAtDesc(List<Long> authorIds);
}
