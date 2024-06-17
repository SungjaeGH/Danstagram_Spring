package com.project.danstagram.domain.follow.repository;

import java.util.List;

public interface FollowRepositoryCustom {

    List<String> findFollowers(String memberId);

    List<String> findFollowings(String memberId);

    Long countFollowers(Long memberIdx);

    Long countFollowings(Long memberIdx);
}
