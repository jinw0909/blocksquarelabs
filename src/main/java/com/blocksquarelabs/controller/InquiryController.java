package com.blocksquarelabs.controller;

import com.blocksquarelabs.dto.InquiryRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api")
public class InquiryController {

    @PostMapping("/inquiry")
    public ResponseEntity<?> createInquiry(@RequestBody InquiryRequest request) {
        log.info("Received inquiry: {}", request);
        return ResponseEntity.ok(request);
    }
}
