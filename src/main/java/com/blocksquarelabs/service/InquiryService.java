package com.blocksquarelabs.service;

import com.blocksquarelabs.domain.Inquiry;
import com.blocksquarelabs.domain.Reply;
import com.blocksquarelabs.domain.ReplyStatus;
import com.blocksquarelabs.dto.admin.InquiryResponse;
import com.blocksquarelabs.dto.InquiryRequest;
import com.blocksquarelabs.dto.admin.ReplyCreateRequest;
import com.blocksquarelabs.repository.InquiryRepository;
import com.blocksquarelabs.repository.ReplyRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InquiryService {

    private final InquiryRepository inquiryRepository;
    private final InquiryMailService inquiryMailService;
    private final ReplyRepository replyRepository;

    @Transactional
    public Inquiry createInquiry(InquiryRequest request) {
        String token = UUID.randomUUID().toString().replace("-", "");

        Inquiry inquiry = Inquiry.create(request, token);

        Inquiry saved = inquiryRepository.save(inquiry);

        // 사용자에게 이메일 인증/접수 확인 메일만 발송
        inquiryMailService.sendInquiryVerifyMail(saved);
        inquiryMailService.sendInquiryMail(saved);

        return saved;
    }

    @Transactional(readOnly = true)
    public Page<InquiryResponse> findAllInquiry(Pageable pageable, Long memberId) {
        Page<Inquiry> inquiryPage = inquiryRepository.findAll(pageable);

        List<Long> inquiryIds = inquiryPage.getContent()
                .stream()
                .map(Inquiry::getId)
                .toList();

        List<Reply> replies = inquiryIds.isEmpty()
                ? List.of()
                : replyRepository.findByInquiryIdsWithCreatedBy(inquiryIds);

        Map<Long, List<Reply>> repliesByInquiryId = replies.stream()
                .collect(Collectors.groupingBy(reply -> reply.getInquiry().getId()));

        return inquiryPage.map(inquiry ->
                InquiryResponse.from(
                        inquiry,
                        repliesByInquiryId.getOrDefault(inquiry.getId(), List.of()),
                        memberId
                )
        );
    }

    @Transactional(readOnly = true)
    public Inquiry findOneById(Long id) {
        return inquiryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("could not find inquiry by id: " + id));
    }

    @Transactional
    public void createReply(Long inquiryId, ReplyCreateRequest request) {
        Inquiry inquiry = inquiryRepository.findById(inquiryId)
                .orElseThrow(() -> new IllegalArgumentException("inquiry not found"));

        Reply reply = new Reply(inquiry, request.getContent());

        replyRepository.save(reply);
    }

    @Transactional
    public void sendReply(Long replyId) {
        Reply reply = replyRepository.findByIdWithInquiry(replyId)
                .orElseThrow(() -> new IllegalArgumentException("답변을 찾을 수 없습니다."));

        Inquiry inquiry = reply.getInquiry();

        inquiryMailService.sendReplyMail(reply);

        reply.markSent();
        inquiry.markReplied();
    }

    @Transactional
    public void updateReply(Long replyId, Long memberId, @Valid ReplyCreateRequest request) {
        Reply reply = replyRepository.findById(replyId)
                .orElseThrow(() -> new IllegalArgumentException("답변을 찾을 수 없습니다."));

        if (!reply.canUpdate(memberId)) {
            throw new IllegalArgumentException("수정할 수 없는 답변입니다.");
        }

        reply.updateContent(request.getContent());
    }

    @Transactional
    public void deleteReply(Long replyId, Long memberId) {
        Reply reply = replyRepository.findById(replyId)
                .orElseThrow(() -> new IllegalArgumentException("답변을 찾을 수 없습니다."));

        if (!reply.canDelete(memberId)) {
            throw new IllegalArgumentException("삭제할 수 없는 답변입니다.");
        }

        replyRepository.delete(reply);
    }

    @Transactional
    public boolean verifyInquiryEmail(String token) {
        Inquiry inquiry = inquiryRepository.findByPublicToken(token)
                .orElse(null);

        if (inquiry == null) {
            return false;
        }

        boolean alreadyVerified = inquiry.isVerified();
        boolean alreadyManagerNotified = inquiry.getManagerNotifiedAt() != null;

        inquiry.verifyEmail();

        if (!alreadyVerified && !alreadyManagerNotified) {
            inquiryMailService.sendManagerNotificationMail(inquiry);
            inquiry.markManagerNotified();
        }

        return true;
    }

    @Transactional
    public void notifyManager(Long inquiryId) {
        Inquiry inquiry = inquiryRepository.findById(inquiryId)
                .orElseThrow(() -> new IllegalArgumentException("inquiry not found"));

        inquiryMailService.sendManagerNotificationMail(inquiry);
        inquiry.markManagerNotified();
    }

    @Transactional
    public Inquiry viewInquiryByPublicToken(String token) {
        Inquiry inquiry = inquiryRepository.findByPublicTokenWithReplies(token)
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 링크입니다."));

        inquiry.getReplies().stream()
                .filter(reply -> reply.getStatus() == ReplyStatus.SENT)
                .forEach(Reply::markViewed);

        return inquiry;
    }

    @Transactional
    public void createAndSendReply(Long inquiryId, @Valid ReplyCreateRequest request) {
        Inquiry inquiry = inquiryRepository.findById(inquiryId)
                .orElseThrow(() -> new IllegalArgumentException("inquiry not found"));

        Reply reply = new Reply(inquiry, request.getContent());

        replyRepository.save(reply);

        inquiryMailService.sendReplyMail(reply);

        reply.markSent();
        inquiry.markReplied();
    }
}