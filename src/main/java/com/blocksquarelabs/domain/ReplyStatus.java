package com.blocksquarelabs.domain;

public enum ReplyStatus {
    DRAFT,      // 작성됨, 아직 메일 전송 전
    SENT,       // 메일 전송 완료
    UPDATED
}
