package com.blocksquarelabs.repository;

import com.blocksquarelabs.domain.Reply;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ReplyRepository extends JpaRepository<Reply, Long> {

    @Query("""
        select r
        from Reply r
        join fetch r.createdBy
        where r.inquiry.id in :inquiryIds
        order by r.createdAt asc
    """)
    List<Reply> findByInquiryIdsWithCreatedBy(List<Long> inquiryIds);

//    Optional<Reply> findByViewToken(String viewToken);

    @Query("""
        select r
        from Reply r
        join fetch r.inquiry i
        where r.id = :replyId
    """)
    Optional<Reply> findByIdWithInquiry(@Param("replyId") Long replyId);
}
