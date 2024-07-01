package com.project.danstagram.domain.dm.repository;

import com.project.danstagram.domain.dm.entity.Dm;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DmRepository extends JpaRepository<Dm, Long> {
}
