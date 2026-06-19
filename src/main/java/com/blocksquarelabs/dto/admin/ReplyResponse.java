package com.blocksquarelabs.dto.admin;

import com.blocksquarelabs.domain.Reply;
import com.blocksquarelabs.domain.ReplyStatus;

import java.time.LocalDateTime;

public record ReplyResponse(
        Long replyId,
        Long createdById,
        String createdByName,
        String content,
        ReplyStatus status,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        LocalDateTime lastSentAt,

        boolean updatable,
        boolean deletable,
        LocalDateTime viewedAt

) {
    public static ReplyResponse from(Reply reply, Long loginMemberId) {
        return new ReplyResponse(
                reply.getId(),
                reply.getCreatedBy().getId(),
                reply.getCreatedBy().getUsername(), // 또는 getName()
                reply.getContent(),
                reply.getStatus(),
                reply.getCreatedAt(),
                reply.getUpdatedAt(),
                reply.getLastSentAt(),
                reply.canUpdate(loginMemberId),
                reply.canDelete(loginMemberId),
                reply.getViewedAt()
        );
    }
}
