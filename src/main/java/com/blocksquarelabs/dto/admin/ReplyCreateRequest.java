package com.blocksquarelabs.dto.admin;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class ReplyCreateRequest {
    @NotBlank(message = "답변 내용을 입력해주세요")
    @Size(max = 5000, message = "답변은 5000자 이하로 입력해주세요.")
    private String content;
}
