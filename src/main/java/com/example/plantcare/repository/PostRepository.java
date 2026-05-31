package com.example.plantcare.repository;
import com.example.plantcare.model.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PostRepository extends JpaRepository<Post, Long> {
    List<Post> findAllByOrderByCreatedAtDesc();
    List<Post> findByAuthorIdOrderByCreatedAtDesc(Long authorId);
    List<Post> findByAuthorIdInOrderByCreatedAtDesc(List<Long> authorIds);
    Page<Post> findByAuthorIdInAndIsVisibleTrueOrderByCreatedAtDesc(List<Long> authorIds, Pageable pageable);
    Page<Post> findByIsVisibleTrueOrderByCreatedAtDesc(Pageable pageable);
    long countByAuthorId(Long authorId);
    Page<Post> findByContentContainingIgnoreCaseAndIsVisibleTrueOrderByCreatedAtDesc(String keyword, Pageable pageable);
}
