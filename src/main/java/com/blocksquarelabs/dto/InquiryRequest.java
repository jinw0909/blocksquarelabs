package com.blocksquarelabs.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class InquiryRequest {

    @NotBlank(message = "이름을 입력해주세요.")
    @Size(max = 50, message = "이름은 50자 이하로 입력해주세요.")
    String name;

    @NotBlank(message = "이메일을 입력해주세요.")
    @Email(message = "올바른 이메일 형식이 아닙니다.")
    @Size(max = 100, message = "이메일은 100자 이하로 입력해주세요.")
    String email;

    @NotBlank(message = "문의 유형을 선택해주세요.")
    @Size(max = 50, message = "문의 유형은 50자 이하로 입력해주세요.")
    String type;

    @NotBlank(message = "문의 내용을 입력해주세요.")
    @Size(max = 2000, message = "문의 내용은 2000자 이하로 입력해주세요.")
    String inquiry;

}
