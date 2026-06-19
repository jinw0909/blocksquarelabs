package com.blocksquarelabs.domain;

import com.blocksquarelabs.dto.InquiryTestRequest;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class InquiryTest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "inquiry_test_id")
    private Long id;

    private String sendTo;

    private String name;

    private String inquiry;

    public static InquiryTest create(InquiryTestRequest request) {
        InquiryTest inquiryTest = new InquiryTest();
        inquiryTest.setSendTo(request.getSendTo());
        inquiryTest.setName(request.getName());
        inquiryTest.setInquiry(request.getInquiry());
        return inquiryTest;
    }

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
