package com.blocksquarelabs.repository;

import com.blocksquarelabs.domain.Inquiry;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface InquiryRepository extends JpaRepository<Inquiry, Long> {

//    Optional<Inquiry> findByVerifyToken(String token);

    @Query("""
        select distinct i
        from Inquiry i
        left join fetch i.replies r
        where i.publicToken = :publicToken
    """)
    Optional<Inquiry> findByPublicTokenWithReplies(@Param("publicToken") String publicToken);

    Optional<Inquiry> findByPublicToken(String token);
}
