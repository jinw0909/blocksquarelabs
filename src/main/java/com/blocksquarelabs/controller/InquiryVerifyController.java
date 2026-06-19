//package com.blocksquarelabs.controller;
//
//import com.blocksquarelabs.service.InquiryService;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Controller;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//
//@Controller
//@RequiredArgsConstructor
//@RequestMapping("/inquiry")
//public class InquiryVerifyController {
//
//    private final InquiryService inquiryService;
//
//    @GetMapping("/verify")
//    public String verify(@RequestParam String token) {
//        boolean verified = inquiryService.verifyInquiryEmail(token);
//
//        if (verified) {
//            return "redirect:/inquiry/verify/success";
//        }
//
//        return "redirect:/inquiry/verify/fail";
//    }
//
//    @GetMapping("/verify/success")
//    public String verifySuccess() {
//        return "inquiry-verify-success";
//    }
//
//    @GetMapping("/verify/fail")
//    public String verifyFail() {
//        return "inquiry-verify-fail";
//    }
//}
//
//
