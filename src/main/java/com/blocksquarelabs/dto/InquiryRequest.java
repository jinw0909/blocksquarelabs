package com.blocksquarelabs.dto;

import lombok.Data;

@Data
public class InquiryRequest {

    String name;
    String email;
    String type;
    String inquiry;

}
