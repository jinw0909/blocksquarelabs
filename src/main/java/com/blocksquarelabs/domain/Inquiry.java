package com.blocksquarelabs.domain;

import com.blocksquarelabs.dto.InquiryRequest;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Inquiry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "inquiry_id")
    private Long id;

    private String name;

    private String email;

    private String type;

    private String inquiry;

    @Enumerated(EnumType.STRING)
    private InquiryStatus status = InquiryStatus.NEW;

    @OneToMany(mappedBy = "inquiry", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Reply> replies = new ArrayList<>();

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private boolean verified = false;

    @Column(length = 100, nullable = false, unique = true)
    private String publicToken;

//    @Column(length = 100, unique = true)
//    private String verifyToken;

    private LocalDateTime verifiedAt;

    private LocalDateTime managerNotifiedAt;

    public static Inquiry create(InquiryRequest request, String publicToken) {
        Inquiry inquiry = new Inquiry();
        inquiry.name = request.getName();
        inquiry.email = request.getEmail();
        inquiry.type = request.getType();
        inquiry.inquiry = request.getInquiry();
        inquiry.status = InquiryStatus.NEW;
        inquiry.publicToken = publicToken;
        return inquiry;
    }

//    public void issueVerifyToken(String token) {
//        this.verifyToken = token;
//        this.verified = false;
//        this.verifiedAt = null;
//    }

    public void verifyEmail() {
        if (this.verified) {
            return;
        }

        this.verified = true;
        this.verifiedAt = LocalDateTime.now();
    }

    public void markReplied() {
        this.status = InquiryStatus.REPLIED;
    }

    public void addReply(Reply reply) {
        this.replies.add(reply);
        reply.setInquiry(this);
    }

    public void markManagerNotified() {
        this.managerNotifiedAt = LocalDateTime.now();
    }

    public boolean isManagerNotified() {
        return this.managerNotifiedAt != null;
    }


}
