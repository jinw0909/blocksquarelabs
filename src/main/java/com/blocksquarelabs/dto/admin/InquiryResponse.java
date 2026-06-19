package com.blocksquarelabs.dto.admin;

import com.blocksquarelabs.domain.Inquiry;
import com.blocksquarelabs.domain.InquiryStatus;
import com.blocksquarelabs.domain.Reply;
import com.blocksquarelabs.domain.ReplyStatus;

import java.time.LocalDateTime;
import java.util.List;

public record InquiryResponse(
        Long inquiryId,
        String name,
        String email,
        String type,
        String content,
        InquiryStatus status,
        LocalDateTime createdAt,
        List<ReplyResponse> replies,
        long draftReplyCount,

        boolean verified,
        LocalDateTime verifiedAt,

        LocalDateTime managerNotifiedAt

) {
    public static InquiryResponse from(Inquiry inquiry, List<Reply> replies, Long memberId) {
        return new InquiryResponse(
                inquiry.getId(),
                inquiry.getName(),
                inquiry.getEmail(),
                inquiry.getType(),
                inquiry.getInquiry(),
                inquiry.getStatus(),
                inquiry.getCreatedAt(),
                replies.stream()
                        .map(reply -> ReplyResponse.from(reply, memberId))
                        .toList(),
                replies.stream()
                        .filter(reply -> reply.getStatus() == ReplyStatus.DRAFT)
                        .count(),
                inquiry.isVerified(),
                inquiry.getVerifiedAt(),
                inquiry.getManagerNotifiedAt()
        );
    }
}
