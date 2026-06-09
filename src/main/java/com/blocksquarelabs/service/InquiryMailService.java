package com.blocksquarelabs.service;

import com.blocksquarelabs.domain.Inquiry;
import com.blocksquarelabs.domain.InquiryTest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class InquiryMailService {

    private final JavaMailSender mailSender;

    @Value("${app.mail.inquiry-to}")
    private String inquiryTo;

    public void sendInquiryMail(Inquiry inquiry) {

        if (inquiry.getEmail() != null && inquiry.getEmail().contains("\n") || inquiry.getEmail().contains("\r")) {
            throw new IllegalArgumentException("Invalid email");
        }

        SimpleMailMessage message = new SimpleMailMessage();

        message.setTo(inquiryTo);
        message.setReplyTo(inquiry.getEmail());

        message.setSubject("[Blocksquare Labs] 새로운 문의가 도착했습니다.");

        message.setText(
            "새로운 문의가 등록되었습니다.\n\n" +
            "이름: " + inquiry.getName() + "\n" +
            "이메일: " + inquiry.getEmail() + "\n" +
            "문의 유형: " + inquiry.getType() + "\n\n" +
            "문의 내용:\n" + inquiry.getInquiry()
        );

        mailSender.send(message);
    }


    public void sendInquiryTestMail(InquiryTest inquiryTest) {


        if (inquiryTest.getSendTo() != null && inquiryTest.getSendTo().contains("\n") || inquiryTest.getSendTo().contains("\r")) {
            throw new IllegalArgumentException("Invalid sendTo");
        }

        SimpleMailMessage message = new SimpleMailMessage();

        message.setTo(inquiryTest.getSendTo());

        message.setSubject("[Test] 새로운 문의가 도착했습니다.");

        message.setText(
                "[Test] 새로운 문의가 등록되었습니다.\n\n" +
                        "이름: " + inquiryTest.getName() + "\n" +
                        "문의 내용:\n" + inquiryTest.getInquiry()
        );

        mailSender.send(message);
    }




}
