package com.project.danstagram.domain.member.repository;

import com.project.danstagram.domain.member.entity.SocialMember;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SocialMemberRepository extends JpaRepository<SocialMember, Long> {
    Optional<SocialMember> findBySocialEmail(String socialEmail);
}
