package com.project.danstagram.domain.member.repository;

import com.project.danstagram.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByMemberIdOrMemberPhoneOrMemberEmail(String memberId, String memberPhone, String memberEmail);

    Boolean existsByMemberId(String memberId);

    Optional<Member> findByMemberId(String memberId);
}
