package com.project.danstagram.domain.follow.repository;


import com.project.danstagram.domain.follow.entity.Follow;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface FollowRepository extends JpaRepository<Follow, Long>, FollowRepositoryCustom {

    @Query("SELECT f FROM Follow f WHERE f.followToUser.memberIdx = :followToUserIdx and f.followFromUser.memberIdx = :followFromUserIdx")
    Optional<Follow> findByFollowToUserAndFollowFromUser (Long followToUserIdx, Long followFromUserIdx);

}
