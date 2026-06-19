package com.blocksquarelabs.controller;

import com.blocksquarelabs.domain.Inquiry;
import com.blocksquarelabs.dto.admin.InquiryResponse;
import com.blocksquarelabs.dto.admin.ReplyCreateRequest;
import com.blocksquarelabs.security.CustomUserDetails;
import com.blocksquarelabs.service.InquiryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final InquiryService inquiryService;

    @GetMapping
    public String admin(@AuthenticationPrincipal UserDetails userDetails,
                        Model model) {

        model.addAttribute("user", userDetails);
        return "admin";
    }

    @GetMapping("/test")
    public String inquiryTest(@AuthenticationPrincipal UserDetails userDetails,
                          Model model) {
        model.addAttribute("user", userDetails);
        return "admin-test";
    }


    @GetMapping("/inquiry")
    public String inquiry(@AuthenticationPrincipal CustomUserDetails userDetails,
                          @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable,
                          Model model) {

        Page<InquiryResponse> inquiryList = inquiryService.findAllInquiry(pageable, userDetails.getMemberId());
        model.addAttribute("inquiryList", inquiryList);
        model.addAttribute("user", userDetails);

        return "inquiry";
    }

    @PostMapping("/inquiry/{id}/reply")
    public String createReply(@PathVariable Long id,
                              @Valid @ModelAttribute ReplyCreateRequest request,
                              BindingResult bindingResult,
                              @RequestParam(defaultValue = "save") String action,
                              @RequestParam(defaultValue = "0") int page,
                              @RequestParam(defaultValue = "10") int size,
                              RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("errorMessage", "답변 내용을 확인해주세요");
            return "redirect:/admin/inquiry?page=" + page + "&size=" + size;
        }

        try {
            if ("send".equals(action)) {
                inquiryService.createAndSendReply(id, request);
                redirectAttributes.addFlashAttribute("successMessage", "답변을 저장하고 메일로 전송했습니다.");
            } else {
                inquiryService.createReply(id, request);
                redirectAttributes.addFlashAttribute("successMessage", "답변을 임시 저장했습니다.");
            }
        } catch (Exception e) {
            if ("send".equals(action)) {
                redirectAttributes.addFlashAttribute("errorMessage", "답변 저장 또는 메일 전송에 실패했습니다.");
            } else {
                redirectAttributes.addFlashAttribute("errorMessage", "답변 저장에 실패했습니다.");
            }
        }

        return "redirect:/admin/inquiry?page=" + page + "&size=" + size;
    }

    @PostMapping("/inquiry/{id}/notify-manager")
    public String notifyManager(@PathVariable Long id,
                                @RequestParam(defaultValue = "0") int page,
                                @RequestParam(defaultValue = "10") int size,
                                RedirectAttributes redirectAttributes) {

        try {
            inquiryService.notifyManager(id);
            redirectAttributes.addFlashAttribute("successMessage", "상사에게 문의 알림 메일을 전송했습니다.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "상사 알림 메일 전송에 실패했습니다.");
        }

        return "redirect:/admin/inquiry?page=" + page + "&size=" + size;
    }

    @PostMapping("/reply/{id}/send")
    public String sendReply(@PathVariable Long id,
                            @RequestParam(defaultValue = "0") int page,
                            @RequestParam(defaultValue = "10") int size,
                            RedirectAttributes redirectAttributes) {

        try {
            inquiryService.sendReply(id);
            redirectAttributes.addFlashAttribute("successMessage", "답변 메일을 전송했습니다.");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "메일 전송에 실패했습니다. " + e.getMessage());
        }

        return "redirect:/admin/inquiry?page=" + page + "&size=" + size;
    }

    @PostMapping("/reply/{id}/update")
    public String updateReply(@AuthenticationPrincipal CustomUserDetails userDetails,
                              @PathVariable Long id,
                              @Valid @ModelAttribute ReplyCreateRequest request,
                              BindingResult bindingResult,
                              @RequestParam(defaultValue = "0") int page,
                              @RequestParam(defaultValue = "10") int size,
                              RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("errorMessage", "답변 내용을 확인해주세요.");
            return "redirect:/admin/inquiry?page=" + page + "&size=" + size;
        }

        try {
            inquiryService.updateReply(id, userDetails.getMemberId(), request);
            redirectAttributes.addFlashAttribute("successMessage", "답변을 수정했습니다.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }

        return "redirect:/admin/inquiry?page=" + page + "&size=" + size;
    }

    @PostMapping("/reply/{id}/delete")
    public String deleteReply(@AuthenticationPrincipal CustomUserDetails userDetails,
                              @PathVariable Long id,
                              @RequestParam(defaultValue = "0") int page,
                              @RequestParam(defaultValue = "10") int size,
                              RedirectAttributes redirectAttributes) {

        try {
            inquiryService.deleteReply(id, userDetails.getMemberId());
            redirectAttributes.addFlashAttribute("successMessage", "답변을 삭제했습니다.");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }

        return "redirect:/admin/inquiry?page=" + page + "&size=" + size;
    }




}
