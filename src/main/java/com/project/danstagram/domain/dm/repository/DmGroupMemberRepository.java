package com.project.danstagram.domain.dm.repository;

import com.project.danstagram.domain.dm.entity.DmGroupMember;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DmGroupMemberRepository extends JpaRepository<DmGroupMember, Long> {
}
