package com.blocksquarelabs.controller;

import com.blocksquarelabs.domain.InquiryTest;
import com.blocksquarelabs.dto.InquiryTestRequest;
import com.blocksquarelabs.service.InquiryTestService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Controller
@RequestMapping("/test")
@RequiredArgsConstructor
public class InquiryTestController {

    private final InquiryTestService inquiryTestService;

    @PostMapping("/inquiry")
    public String test(@Valid @ModelAttribute InquiryTestRequest request,
                       BindingResult bindingResult,
                       @AuthenticationPrincipal UserDetails userDetails,
                       Model model) {

        log.info("Received request: {}", request);
        log.info("Authenticated user: {}", userDetails.getUsername());

        if (bindingResult.hasErrors()) {
            model.addAttribute("user", userDetails);
            return "admin-test";
        }

        InquiryTest result = inquiryTestService.createInquiryTest(request);
        return "redirect:/admin/inquiry?success=true";

    }
}
