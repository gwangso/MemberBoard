package com.icia.memberboard.repository;

import com.icia.memberboard.entity.MemberFileEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberFileRepository extends JpaRepository<MemberFileEntity, Long> {
}
