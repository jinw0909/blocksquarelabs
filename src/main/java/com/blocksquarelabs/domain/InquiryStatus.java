package com.blocksquarelabs.domain;

import lombok.Getter;

@Getter
public enum InquiryStatus {
    NEW("접수"),
    REPLIED("답변 완료");

    private final String label;

    InquiryStatus(String label) {
        this.label = label;
    }

}
