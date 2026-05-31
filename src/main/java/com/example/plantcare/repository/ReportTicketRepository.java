package com.example.plantcare.repository;
import com.example.plantcare.model.ReportTicket;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReportTicketRepository extends JpaRepository<ReportTicket, Long> {
    boolean existsByPost(com.example.plantcare.model.Post post);

    List<ReportTicket> findByPostId(Long postId);
}
