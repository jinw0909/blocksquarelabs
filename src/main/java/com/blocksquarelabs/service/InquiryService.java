package com.blocksquarelabs.service;

import com.blocksquarelabs.domain.Inquiry;
import com.blocksquarelabs.dto.InquiryRequest;
import com.blocksquarelabs.repository.InquiryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class InquiryService {

    private final InquiryRepository inquiryRepository;
    private final InquiryMailService inquiryMailService;

    @Transactional
    public Inquiry createInquiry(InquiryRequest request) {
        Inquiry inquiry = Inquiry.create(request);
        Inquiry savedInquiry =  inquiryRepository.save(inquiry);

        inquiryMailService.sendInquiryMail(savedInquiry);

        return savedInquiry;
    }
}