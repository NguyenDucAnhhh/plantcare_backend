package com.example.plantcare.repository;
import com.example.plantcare.model.LikeAction;
import com.example.plantcare.model.Post;
import com.example.plantcare.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface LikeActionRepository extends JpaRepository<LikeAction, Long> {
    Optional<LikeAction> findByUserAndPost(User user, Post post);
    boolean existsByUser_EmailAndPost(String email, Post post);
}
