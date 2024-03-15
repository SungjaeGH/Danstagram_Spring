package com.project.danstagram.domain.auth.repository;

import com.project.danstagram.domain.auth.entity.SocialMember;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AuthRepository extends JpaRepository<SocialMember, Long> {
    Optional<SocialMember> findBySocialEmail(String socialEmail);
}
