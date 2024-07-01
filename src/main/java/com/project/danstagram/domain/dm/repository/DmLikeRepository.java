package com.project.danstagram.domain.dm.repository;

import com.project.danstagram.domain.dm.entity.DmLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface DmLikeRepository extends JpaRepository<DmLike, Long> {

    @Query("SELECT dl FROM DmLike dl WHERE dl.dm.dmIdx = :dmIdx and dl.member.memberIdx = :memberIdx")
    Optional<DmLike> findByDmIdxAndMemberIdx(Long dmIdx, Long memberIdx);

}
