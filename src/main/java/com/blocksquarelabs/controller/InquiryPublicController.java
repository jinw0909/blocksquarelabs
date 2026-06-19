package com.blocksquarelabs.controller;

import com.blocksquarelabs.domain.Inquiry;
import com.blocksquarelabs.domain.Reply;
import com.blocksquarelabs.domain.ReplyStatus;
import com.blocksquarelabs.service.InquiryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/inquiry")
public class InquiryPublicController {

    private final InquiryService inquiryService;

    @GetMapping("/status")
    public String status(@RequestParam String token, Model model) {
        try {
            Inquiry inquiry = inquiryService.viewInquiryByPublicToken(token);

            List<Reply> sentReplies = inquiry.getReplies().stream()
                    .filter(reply -> reply.getStatus() == ReplyStatus.SENT)
                    .toList();

            model.addAttribute("inquiry", inquiry);
            model.addAttribute("replies", sentReplies);

            return "inquiry-status";

        } catch (IllegalArgumentException e) {
            return "redirect:/inquiry/verify/invalid";
        }
    }

    @GetMapping("/verify")
    public String verify(@RequestParam String token) {
        boolean verified = inquiryService.verifyInquiryEmail(token);

        if (!verified) {
            return "redirect:/inquiry/verify/invalid";
        }

        return "redirect:/inquiry/status?token=" + token;
    }

    @GetMapping("/verify/invalid")
    public String verifyFail() {
        return "inquiry-verify-invalid";
    }


}
