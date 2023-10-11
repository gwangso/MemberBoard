package com.icia.memberboard.repository;

import com.icia.memberboard.entity.BoardEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BoardRepository extends JpaRepository<BoardEntity, Long> {

    Page<BoardEntity> findByBoardTitleContaining(String query, Pageable pageable);

    Page<BoardEntity> findByBoardWriterContaining(String query, Pageable pageable);
}
