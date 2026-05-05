package com.example.plantcare.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "care_tips")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CareTip {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Khá»›p Äáº·c Táº£ "Ná»™i dung bá»‹ bá» trá»‘ng D-1..."
    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String content;

    @Column(nullable = false)
    private String imageUrl;

    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
