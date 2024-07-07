package com.project.danstagram.domain.search.repository;

import com.project.danstagram.domain.search.entity.Search;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SearchRepository extends JpaRepository<Search, Long> {
}
