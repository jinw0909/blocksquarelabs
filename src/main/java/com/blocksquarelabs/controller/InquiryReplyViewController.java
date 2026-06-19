//package com.blocksquarelabs.controller;
//
//import com.blocksquarelabs.domain.Reply;
//import com.blocksquarelabs.service.InquiryService;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Controller;
//import org.springframework.ui.Model;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//
//@Controller
//@RequiredArgsConstructor
//@RequestMapping("/inquiry/reply")
//public class InquiryReplyViewController {
//
//    private final InquiryService inquiryService;
//
//    @GetMapping("/view")
//    public String viewReply(@RequestParam String token,
//                            Model model) {
//        Reply reply = inquiryService.viewReplyByToken(token);
//
//        model.addAttribute("reply", reply);
//        model.addAttribute("inquiry", reply.getInquiry());
//
//        return "reply-view";
//    }
//}
