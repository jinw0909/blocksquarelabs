package com.blocksquarelabs.service;

import com.blocksquarelabs.domain.InquiryTest;
import com.blocksquarelabs.dto.InquiryTestRequest;
import com.blocksquarelabs.repository.InquiryTestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class InquiryTestService {

    private final InquiryMailService inquiryMailService;
    private final InquiryTestRepository inquiryTestRepository;
    @Transactional
    public InquiryTest createInquiryTest(InquiryTestRequest request) {

        InquiryTest inquiryTest = InquiryTest.create(request);
        InquiryTest saved = inquiryTestRepository.save(inquiryTest);

        inquiryMailService.sendInquiryTestMail(inquiryTest);

        return saved;
    }
}
