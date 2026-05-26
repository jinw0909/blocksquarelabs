package com.blocksquarelabs.controller;

import com.blocksquarelabs.domain.Inquiry;
import com.blocksquarelabs.dto.InquiryRequest;
import com.blocksquarelabs.service.InquiryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class InquiryController {

    private final InquiryService inquiryService;

    @PostMapping("/inquiry")
    public ResponseEntity<?> createInquiry(@Valid @RequestBody InquiryRequest request) {

        log.info("Received inquiry: {}", request);
        if (request.getWebsite() != null && !request.getWebsite().isBlank()) {
            //봇으로 판단
            //티 안나게 200 OK로 반환
            return ResponseEntity.ok(Map.of("message", "success"));
        }

        Inquiry result = inquiryService.createInquiry(request);
        return ResponseEntity.ok(result);
    }
}
