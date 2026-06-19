package com.blocksquarelabs.service;

import com.blocksquarelabs.domain.Inquiry;
import com.blocksquarelabs.domain.InquiryTest;
import com.blocksquarelabs.domain.Reply;
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

    @Value("${app.base-url}")
    private String baseUrl;

    @Value("${app.mail.manager-to}")
    private String managerTo;

    public void sendInquiryMail(Inquiry inquiry) {
        validateEmail(inquiry.getEmail());

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

    public void sendInquiryVerifyMail(Inquiry inquiry) {
        validateEmail(inquiry.getEmail());
        validatePublicToken(inquiry);

        String verifyUrl = baseUrl + "/inquiry/verify?token=" + inquiry.getPublicToken();

        SimpleMailMessage message = new SimpleMailMessage();

        message.setTo(inquiry.getEmail());
        message.setSubject("[Blocksquare Labs] 문의 접수 확인");

        message.setText(
                inquiry.getName() + "님, 안녕하세요.\n\n" +
                        "Blocksquare Labs에 문의를 남겨주셔서 감사합니다.\n\n" +
                        "정확한 답변 안내를 위해 아래 링크를 클릭해 이메일 인증을 완료해 주세요.\n\n" +
                        verifyUrl + "\n\n" +
                        "이메일 인증이 완료되면 담당자가 문의 내용을 확인한 뒤 답변을 안내드립니다.\n\n" +
                        "본인이 요청하지 않은 문의라면 이 메일을 무시해주세요.\n\n" +
                        "감사합니다.\n" +
                        "Blocksquare Labs"
        );

        mailSender.send(message);
    }

//    public void sendReplyMail(Reply reply) {
//        Inquiry inquiry = reply.getInquiry();
//
//        validateEmail(inquiry.getEmail());
//        validatePublicToken(inquiry);
//
//        String statusUrl = baseUrl + "/inquiry/status?token=" + inquiry.getPublicToken();
//
//        SimpleMailMessage message = new SimpleMailMessage();
//
//        message.setTo(inquiry.getEmail());
//        message.setSubject("[Blocksquare Labs] 문의 답변이 등록되었습니다.");
//
//        message.setText(
//                inquiry.getName() + "님, 안녕하세요.\n\n" +
//                        "Blocksquare Labs에 남겨주신 문의에 대한 답변이 등록되었습니다.\n\n" +
//                        "아래 링크에서 문의 처리 상태와 답변 내용을 확인해주세요.\n\n" +
//                        statusUrl + "\n\n" +
//                        "감사합니다.\n" +
//                        "Blocksquare Labs"
//        );
//
//        mailSender.send(message);
//    }

    public void sendReplyMail(Reply reply) {
        Inquiry inquiry = reply.getInquiry();

        validateEmail(inquiry.getEmail());
        validatePublicToken(inquiry);

        String statusUrl = baseUrl + "/inquiry/status?token=" + inquiry.getPublicToken();

        SimpleMailMessage message = new SimpleMailMessage();

        message.setTo(inquiry.getEmail());
        message.setSubject("[Blocksquare Labs] 문의 답변 안내");

        message.setText(
                inquiry.getName() + "님, 안녕하세요.\n\n" +
                        "Blocksquare Labs에 남겨주신 문의에 대한 답변을 아래와 같이 안내드립니다.\n\n" +
                        "[답변 내용]\n" +
                        preview(reply.getContent()) + "\n\n" +
                        "문의 접수 정보와 답변 내용은 아래 링크에서도 다시 확인하실 수 있습니다.\n\n" +
                        statusUrl + "\n\n" +
                        "감사합니다.\n" +
                        "Blocksquare Labs"
        );

        mailSender.send(message);
    }

    private String preview(String text) {
        if (text == null || text.isBlank()) {
            return "답변 내용이 등록되었습니다.";
        }

        String normalized = text.strip();

        if (normalized.length() <= 500) {
            return normalized;
        }

        return normalized.substring(0, 500) + "\n\n... 전체 답변은 아래 링크에서 확인하실 수 있습니다.";
    }

    public void sendInquiryTestMail(InquiryTest inquiryTest) {
        validateEmail(inquiryTest.getSendTo());

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

    private void validatePublicToken(Inquiry inquiry) {
        if (inquiry.getPublicToken() == null || inquiry.getPublicToken().isBlank()) {
            throw new IllegalStateException("Public token is not issued");
        }

        if (inquiry.getPublicToken().contains("\n") || inquiry.getPublicToken().contains("\r")) {
            throw new IllegalArgumentException("Invalid public token");
        }
    }

    private void validateEmail(String email) {
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("Invalid email");
        }

        if (email.contains("\n") || email.contains("\r")) {
            throw new IllegalArgumentException("Invalid email");
        }
    }


    public void sendManagerNotificationMail(Inquiry inquiry) {
        validateEmailRecipients(managerTo);

        SimpleMailMessage message = new SimpleMailMessage();

        message.setTo(splitEmails(managerTo));
        message.setReplyTo(inquiry.getEmail());

        message.setSubject("[Blocksquare Labs] 확인이 필요한 문의가 전달되었습니다.");

        message.setText(
                "확인이 필요한 문의가 전달되었습니다.\n\n" +
                        "이름: " + inquiry.getName() + "\n" +
                        "이메일: " + inquiry.getEmail() + "\n" +
                        "문의 유형: " + inquiry.getType() + "\n" +
                        "이메일 인증 여부: " + (inquiry.isVerified() ? "인증됨" : "미인증") + "\n\n" +
                        "문의 내용:\n" +
                        inquiry.getInquiry() + "\n\n" +
                        "필요 시 홈페이지 관리자에게 문의 처리 방향을 전달해 주세요.\n\n" +
                        "Blocksquare Labs"
        );

        mailSender.send(message);
    }

    private String[] splitEmails(String emails) {
        return emails.split("\\s*,\\s*");
    }

    private void validateEmailRecipients(String emails) {
        if (emails == null || emails.isBlank()) {
            throw new IllegalArgumentException("Invalid email recipients");
        }

        if (emails.contains("\n") || emails.contains("\r")) {
            throw new IllegalArgumentException("Invalid email recipients");
        }

        for (String email : splitEmails(emails)) {
            validateEmail(email);
        }
    }
}