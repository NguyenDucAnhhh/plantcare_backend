package com.example.plantcare.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalTime;
import java.time.LocalDateTime;

@Entity
@Table(name = "reminders")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Reminder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Loại báo thức: Tưới nước, bón phân
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReminderType type;

    // Giờ sẽ rung chuông báo thức (Giống hệt app Đồng Hồ báo thức trên IOS/Android)
    // Lưu ý: LocalTime chỉ lưu Giờ:Phút, không dính đến Ngày/Tháng.
    // Ví dụ: 07:30
    @Column(nullable = false)
    private LocalTime triggerTime;

    // Quy tắc điểm danh lặp lại tuần hoàn: VD "MON,WED,FRI", "EVERYDAY", hoặc "NONE"
    @Column(nullable = false)
    private String repeatDays;

    // Cái công tắc Nút gạt màu xanh (Toggle) trong App. 
    // Tắt công tắc thì Server tự hiểu không gửi thông báo Push nữa.
    @Column(nullable = false)
    private boolean isActive = true;

    @Column(nullable = true)
    private LocalDateTime lastPerformed;

    // Lịch Hẹn này dành cho cái Cây cụ thể nào?
    // MANY_TO_ONE: 1 Cái cây có thể cài tận 3-4 loại Báo thức khác nhau (1 báo thức tưới, 1 báo thức tỉa cành...)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plant_id", nullable = false)
    @ToString.Exclude
    private Plant plant;
}
