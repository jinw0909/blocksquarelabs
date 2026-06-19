package com.blocksquarelabs.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Getter
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Reply {

    @Id
    @Column(name = "reply_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "inquiry_id", nullable = false)
    private Inquiry inquiry;

    @CreatedBy
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false, updatable = false)
    private Member createdBy;

    @Lob
    @Column(nullable = false)
    private String content;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ReplyStatus status = ReplyStatus.DRAFT;

    private LocalDateTime lastSentAt;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    private LocalDateTime viewedAt;

    public Reply(Inquiry inquiry, String content) {
        this.inquiry = inquiry;
        this.content = content;
    }

    public void updateContent(String content) {
        if (this.status != ReplyStatus.DRAFT) {
            throw new IllegalStateException("이미 전송된 답변은 수정할 수 없습니다.");
        }

        this.content = content;
    }

    public void markSent() {
        this.status = ReplyStatus.SENT;
        this.lastSentAt = LocalDateTime.now();
    }

    public boolean isWrittenBy(Long memberId) {
        return this.createdBy != null
                && this.createdBy.getId().equals(memberId);
    }

    public boolean canUpdate(Long memberId) {
        return isWrittenBy(memberId)
                && this.status == ReplyStatus.DRAFT;
    }

    public boolean canDelete(Long memberId) {
        return isWrittenBy(memberId)
                && this.status == ReplyStatus.DRAFT;
    }

//    public void issueViewToken(String token) {
//        this.viewToken = token;
//    }

    public void markViewed() {
        this.viewedAt = LocalDateTime.now();
    }


}
